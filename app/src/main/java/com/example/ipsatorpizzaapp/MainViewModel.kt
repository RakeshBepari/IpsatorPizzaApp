package com.example.ipsatorpizzaapp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ipsatorpizzaapp.data.remote.responses.PizzaDto
import com.example.ipsatorpizzaapp.data.remote.responses.Size
import com.example.ipsatorpizzaapp.data.remote.responses.getpizzadto
import com.example.ipsatorpizzaapp.data.repositories.PizzaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


data class PizzaUiState(
    val pizzaDto: PizzaDto = getpizzadto(),
    var crustSizeMap: MutableMap<String, List<String>> = mutableMapOf(),
    var currentPrice: Int = 235,
    var cartItemList :MutableList<CartItem> = mutableListOf()

    )

sealed class UiEvent() {
    data class Selected(val selectedCrust: String,val selectedSize: String) : UiEvent()
    data class AddItemToCart(val cartItem: CartItem):UiEvent()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PizzaRepository
) : ViewModel() {

    var pizzaUiState by mutableStateOf(PizzaUiState())

    var crustSizeList: MutableMap<String, List<String>> = mutableMapOf()


    init {
        viewModelScope.launch {

            val p = async { getPizza() }
            p.await()

            createCrustSizeList()
        }
    }

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.Selected -> {
                getPrice(event.selectedCrust, event.selectedSize)
            }
            is UiEvent.AddItemToCart -> {
                val newCartList:MutableList<CartItem> = mutableListOf()
                newCartList.addAll(pizzaUiState.cartItemList)
                var quant = 1
                val price = pizzaUiState.currentPrice
                newCartList.forEach { cartItem->
                    if (cartItem.selectedCrust == event.cartItem.selectedCrust && cartItem.selectedSize == event.cartItem.selectedSize) {
                        quant = cartItem.quantity + 1
                        newCartList.remove(cartItem)
                    }
                }

                val newCartItem = event.cartItem.copy(quantity = quant, price = price*quant)

                newCartList.add(newCartItem)

                pizzaUiState= pizzaUiState.copy(
                    cartItemList = newCartList.distinct().toMutableList()
                )
            }

        }
    }

    private fun getPrice(selectedCrust: String, selectedSize: String) {
        pizzaUiState.pizzaDto.crusts.forEach { crust ->
            if (crust.name == selectedCrust) {
                crust.sizes.forEach { size ->
                    if (size.name == selectedSize) {
                        pizzaUiState = pizzaUiState.copy(currentPrice = size.price)
                    }
                }
            }
        }
    }

    private fun createCrustSizeList() {
        pizzaUiState.pizzaDto.crusts.forEach { crust ->
            crustSizeList[crust.name] = giveSizes(crust.sizes)
            Log.d("MainViewModel", crustSizeList.toString())
        }
//        Log.d("CrustSizeList",crustSizeList.toString())
        pizzaUiState = pizzaUiState.copy(
            crustSizeMap = crustSizeList
        )
    }

    private fun giveSizes(sizes: List<Size>): List<String> {
        val list = mutableListOf<String>()
        sizes.forEach { size ->
            list.add(size.name)
        }
        return list
    }

    private fun getPizza() = viewModelScope.launch {
        val newPizza = repository.getPizza()
        pizzaUiState = pizzaUiState.copy(
            pizzaDto = PizzaDto(
                crusts = newPizza.crusts,
                defaultCrust = newPizza.defaultCrust,
                description = newPizza.description,
                id = newPizza.id,
                isVeg = newPizza.isVeg,
                name = newPizza.name
            )
        )

        Log.d("MainViewModel", pizzaUiState.toString())
    }
}