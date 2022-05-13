package com.example.ipsatorpizzaapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ipsatorpizzaapp.ui.theme.IpsatorPizzaAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IpsatorPizzaAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting(
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

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)){
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
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
                }

            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.BottomEnd)
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Total Quantity: ${uiState.totalQuantity}")
                Text(text = "Total Price: ₹ ${uiState.totalPrice}")
            }
        }


        


    }
}

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
        Column {
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

@Composable
fun ShoppingCart(
    list: List<CartItem>,
    addQuantity: (CartItem) -> Unit,
    reduceQuantity: (CartItem) -> Unit,
    removeItem: (CartItem) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        items(list) { item ->
            Column(modifier = Modifier.wrapContentHeight()) {
                Text(text = "Name: "+item.name)
                Text(text = "Desc: "+item.description)
                Text(text = "isVeg: "+item.isVeg.toString())
                Text(text = "Crust: "+item.selectedCrust)
                Text(text = "Size: "+item.selectedSize)
                Text(text = "Price: "+item.price.toString())

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(0.7f)) {

                    IconButton(onClick = {
                        if (item.quantity == 1) {
                            removeItem(item)
                        } else {
                            reduceQuantity(item)
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_remove_circle),
                            contentDescription = "Remove"
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Quan: "+item.quantity.toString())
                        Button(onClick = { removeItem(item) }) {
                            Text(text = "Remove")
                        }
                    }

                    IconButton(onClick = {
                        addQuantity(item)
                        Log.d("AddButtonClicked", item.toString())
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_circle),
                            contentDescription = "Add"
                        )
                    }
                }

            }

        }
    }
}

@Composable
fun CrustSizeDropdown(
    crustSizeList: MutableMap<String, List<String>>,
    selected: (String, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {

        var selectedKey by remember {
            mutableStateOf("Hand-tossed")
        }
        var selectedValue by remember {
            mutableStateOf("Regular")
        }

        CustomDropdown(
            widthSize = 0.5f,
            list = crustSizeList.keys.toList(),
            selectedText = selectedKey
        ) {
            selectedKey = it
            selectedValue = crustSizeList[it]?.get(0) ?: ""
            selected(selectedKey, selectedValue)
        }


        CustomDropdown(
            widthSize = 0.5f,
            list = if (selectedKey == "") listOf("Size") else crustSizeList[selectedKey]!!,
            selectedText = selectedValue
        ) {
            selectedValue = it
            selected(selectedKey, selectedValue)
        }
    }
}

@Composable
fun CustomDropdown(
    widthSize: Float,
    list: List<String>,
    selectedText: String,
    selected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier
            .fillMaxWidth(widthSize)
            .clickable(onClick = { expanded = true })
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = selectedText
        )


        Icon(
            painter = painterResource(
                if (expanded)
                    R.drawable.ic_up_arrow
                else
                    R.drawable.ic_down_arrow
            ),
            contentDescription = null,
            modifier = Modifier
                .size(12.dp, 12.dp)
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
        },
        modifier = Modifier
            .fillMaxWidth(widthSize)
    )
    {
        list.forEachIndexed { index, text ->
            DropdownMenuItem(
                onClick = {
                    selectedIndex = index
                    selected(list[selectedIndex])
                    expanded = false
                }
            ) {
                Text(
                    text = text
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    IpsatorPizzaAppTheme {
        Greeting()
    }
}