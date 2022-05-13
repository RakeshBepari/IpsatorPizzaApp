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
    var cartItemList: MutableList<CartItem> = mutableListOf(),
    val totalQuantity: Int = 0,
    val totalPrice: Int = 0

)

sealed class UiEvent() {
    data class Selected(val selectedCrust: String, val selectedSize: String) : UiEvent()
    data class AddItemToCart(val cartItem: CartItem) : UiEvent()
    data class AddQuantity(val cartItem: CartItem) : UiEvent()
    data class RemoveItemFromCart(val cartItem: CartItem) : UiEvent()
    data class ReduceQuantity(val cartItem: CartItem) : UiEvent()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PizzaRepository
) : ViewModel() {

    var pizzaUiState by mutableStateOf(PizzaUiState())

    private var crustSizeList: MutableMap<String, List<String>> = mutableMapOf()


    private var totalQuantity = 0
    private var totalPrice = 0

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
                val newCartList: MutableList<CartItem> = mutableListOf()
                newCartList.addAll(pizzaUiState.cartItemList)
                var quant = 1
                val price = getCurrentItemPrice(event.cartItem)
                newCartList.forEach { cartItem ->
                    if (cartItem.selectedCrust == event.cartItem.selectedCrust && cartItem.selectedSize == event.cartItem.selectedSize) {
                        quant = cartItem.quantity + 1
                        newCartList.remove(cartItem)
                    }
                }

                val newCartItem = event.cartItem.copy(quantity = quant, price = price * quant)

                if (newCartList.contains(event.cartItem.copy(quantity = quant - 1))) {
                    newCartList.remove(event.cartItem.copy(quantity = quant - 1))
                }

                newCartList.add(newCartItem)


                calculateTotalQuantityPrice(newCartList)
                pizzaUiState = pizzaUiState.copy(
                    cartItemList = newCartList.distinct().toMutableList(),
                    totalQuantity = totalQuantity,
                    totalPrice = totalPrice
                )
            }
            is UiEvent.RemoveItemFromCart -> {

                val newCartList: MutableList<CartItem> = mutableListOf()
                newCartList.addAll(pizzaUiState.cartItemList)

                newCartList.remove(event.cartItem)

                calculateTotalQuantityPrice(newCartList)
                pizzaUiState = pizzaUiState.copy(
                    cartItemList = newCartList.distinct().toMutableList(),
                    totalQuantity = totalQuantity,
                    totalPrice = totalPrice
                )
            }
            is UiEvent.ReduceQuantity -> {
                val newCartList: MutableList<CartItem> = mutableListOf()
                newCartList.addAll(pizzaUiState.cartItemList)


                val newItem = event.cartItem.copy(
                    quantity = event.cartItem.quantity - 1,
                    price = getCurrentItemPrice(event.cartItem) * (event.cartItem.quantity - 1)
                )


                newCartList.remove(event.cartItem)

                newCartList.add(newItem)

                calculateTotalQuantityPrice(newCartList)
                pizzaUiState = pizzaUiState.copy(
                    cartItemList = newCartList.distinct().toMutableList(),
                    totalQuantity = totalQuantity,
                    totalPrice = totalPrice
                )
            }
            is UiEvent.AddQuantity -> {
                val newCartList: MutableList<CartItem> = mutableListOf()
                newCartList.addAll(pizzaUiState.cartItemList)


                val newItem = event.cartItem.copy(
                    quantity = event.cartItem.quantity + 1,
                    price = getCurrentItemPrice(event.cartItem) * (event.cartItem.quantity + 1)
                )

                newCartList.remove(event.cartItem)

                newCartList.add(newItem)

                calculateTotalQuantityPrice(newCartList)
                pizzaUiState = pizzaUiState.copy(
                    cartItemList = newCartList.distinct().toMutableList(),
                    totalQuantity = totalQuantity,
                    totalPrice = totalPrice
                )
            }
        }
    }

    private fun calculateTotalQuantityPrice(list: List<CartItem>) {
        totalQuantity = 0
        totalPrice = 0
        list.forEach {
            totalQuantity += it.quantity
            totalPrice += it.price
        }
    }

    private fun getCurrentItemPrice(cartItem: CartItem): Int {
        return cartItem.price / cartItem.quantity
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