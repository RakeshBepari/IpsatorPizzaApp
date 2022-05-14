package com.example.ipsatorpizzaapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ipsatorpizzaapp.domain.CartItem
import com.example.ipsatorpizzaapp.R

@Composable
fun ShoppingCart(
    list: List<CartItem>,
    addQuantity: (CartItem) -> Unit,
    reduceQuantity: (CartItem) -> Unit,
    removeItem: (CartItem) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(50.dp),
        modifier = Modifier.padding(top = 6.dp, bottom = 50.dp, start = 8.dp,end=8.dp)
    ) {
        items(list) { item ->
            Column(modifier = Modifier.wrapContentHeight(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Name: " + item.name)
                Text(text = "Desc: " + item.description)
                Text(text = "isVeg: " + item.isVeg.toString())
                Text(text = "Crust: " + item.selectedCrust)
                Text(text = "Size: " + item.selectedSize)
                Text(text = "Price: " + item.price.toString())

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
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Quan: " + item.quantity.toString())
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