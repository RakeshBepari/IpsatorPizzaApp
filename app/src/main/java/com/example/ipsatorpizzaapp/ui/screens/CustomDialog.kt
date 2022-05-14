package com.example.ipsatorpizzaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ipsatorpizzaapp.MainViewModel
import com.example.ipsatorpizzaapp.domain.CartItem
import com.example.ipsatorpizzaapp.ui.PizzaUiState
import com.example.ipsatorpizzaapp.ui.UiEvent

@Composable
fun CustomDialog(
    mainViewModel: MainViewModel,
    uiState: PizzaUiState,
    crustSizeList: MutableMap<String, List<String>>,
    selectedList: (String, String) -> Unit,
    selectedUI: UiEvent.Selected,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .background(Color.DarkGray, RoundedCornerShape(10.dp))
                .padding(16.dp)
        ) {
            Text(text = "name: Non-veg-pizza")
            Text(text = "isVeg: false")
            Text(text = "description: Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")





            CrustSizeDropdown(crustSizeList = crustSizeList) { selectedCrust, selectedSize ->

                selectedList(selectedCrust, selectedSize)

            }
            Text(text = "Price: ₹ ${uiState.currentPrice}")

            Button(onClick = {

                val cartItem = CartItem(
                    name = uiState.pizzaDto.name,
                    isVeg = uiState.pizzaDto.isVeg,
                    description = uiState.pizzaDto.description,
                    selectedCrust = selectedUI.selectedCrust,
                    selectedSize = selectedUI.selectedSize,
                    price = uiState.currentPrice,
                    quantity = 1
                )

                mainViewModel.onEvent(UiEvent.AddItemToCart(cartItem))
                onDismiss()


            }) {
                Text(text = "Add to Cart")

            }


        }
    }
}