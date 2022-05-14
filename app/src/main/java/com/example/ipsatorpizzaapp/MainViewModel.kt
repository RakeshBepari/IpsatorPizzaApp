package com.example.ipsatorpizzaapp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ipsatorpizzaapp.data.remote.responses.PizzaDto
import com.example.ipsatorpizzaapp.data.remote.responses.Size
import com.example.ipsatorpizzaapp.data.repositories.PizzaRepository
import com.example.ipsatorpizzaapp.domain.CartItem
import com.example.ipsatorpizzaapp.ui.PizzaUiState
import com.example.ipsatorpizzaapp.ui.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


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

            /**
             * Changes the state of current selected crust and size
             * */
            is UiEvent.Selected -> {
                getPrice(event.selectedCrust, event.selectedSize)
            }

            /**
             * Adds an item to the cart while checking if the item is already available if so increasing the count and updating
             * the price of the item and also updating the total quantity and total price
             * */
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

            /**
             * It removes the items form the cart even if there are multiple quantities of it available
             * */
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

            /**
             * Decreases the quantity of an item in the cart while checking if the item has only one quantity left
             * if so removing the item completely and updating the price according
             * It also updates the total quantity and total price
             * */
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

            /**
             * Increases the quantity of an item in the cart
             * and updating the price according
             * It also updates the total quantity and total price
             * */
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


    /**
     * It recalculates the total quantity and total price everytime its called
     * As and when values are add or removed this function is called
     * */
    private fun calculateTotalQuantityPrice(list: List<CartItem>) {
        totalQuantity = 0
        totalPrice = 0
        list.forEach {
            totalQuantity += it.quantity
            totalPrice += it.price
        }
    }

    /**
     * Gives the single quantity price of the current cart item
     * */
    private fun getCurrentItemPrice(cartItem: CartItem): Int {
        return cartItem.price / cartItem.quantity
    }

    /**
     * Updates the state of the screen.
     * Updates the current price which needs to be shown according to the selectedCrust and selectedSize
     * */
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


    /**
     * Map the corresponding the crust to their corresponding sizes
     * */
    private fun createCrustSizeList() {
        pizzaUiState.pizzaDto.crusts.forEach { crust ->
            crustSizeList[crust.name] = giveSizes(crust.sizes)
            Log.d("MainViewModel", crustSizeList.toString())
        }
        pizzaUiState = pizzaUiState.copy(
            crustSizeMap = crustSizeList
        )
    }

    /**
     * Makes a list of sizes by its name and gives the list
     * */
    private fun giveSizes(sizes: List<Size>): List<String> {
        val list = mutableListOf<String>()
        sizes.forEach { size ->
            list.add(size.name)
        }
        return list
    }

    /**
     * Gets the pizza from a remote API
     * */
    private fun getPizza() = viewModelScope.launch{
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