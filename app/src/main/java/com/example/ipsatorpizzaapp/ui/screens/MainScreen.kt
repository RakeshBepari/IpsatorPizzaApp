package com.example.ipsatorpizzaapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ipsatorpizzaapp.MainViewModel
import com.example.ipsatorpizzaapp.R
import com.example.ipsatorpizzaapp.ui.UiEvent


@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val uiState = mainViewModel.pizzaUiState
    var show by remember {
        mutableStateOf(false)
    }

    val crustSizeList: MutableMap<String, List<String>> = mutableMapOf(
        "Hand-tossed" to listOf("Regular", "Medium", "Large"),
        "Cheese Burst" to listOf("Medium", "Large")
    )

    var selected by remember {
        mutableStateOf(UiEvent.Selected(selectedCrust = "Hand-tossed", selectedSize = "Regular"))
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = { show = !show },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 150.dp, height = 50.dp)
        ) {
            Text(text = "Add A Pizza")
        }
        if (show) {
            CustomDialog(
                mainViewModel,
                uiState,
                crustSizeList,
                selectedUI = selected,
                selectedList = { selectedCrust, selectedSize ->
                    selected = UiEvent.Selected(selectedCrust, selectedSize)
                    mainViewModel.onEvent(
                        UiEvent.Selected(
                            selectedCrust = selectedCrust,
                            selectedSize = selectedSize
                        )
                    )
                },
                onDismiss = {
                    show = false
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (uiState.cartItemList.isNotEmpty()) {
                    ShoppingCart(
                        list = uiState.cartItemList,
                        addQuantity = {
                            mainViewModel.onEvent(UiEvent.AddQuantity(cartItem = it))
                        },
                        reduceQuantity = {
                            mainViewModel.onEvent(UiEvent.ReduceQuantity(cartItem = it))
                        },
                        removeItem = {
                            mainViewModel.onEvent(UiEvent.RemoveItemFromCart(cartItem = it))
                        }
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_amico),
                        contentDescription = "Cart Empty"
                    )
                }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.BottomEnd)
                    .padding(2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total Quantity: ${uiState.totalQuantity}")
                Text(text = "Total Price: ₹ ${uiState.totalPrice}")
            }
        }


    }
}