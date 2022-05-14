package com.example.ipsatorpizzaapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ipsatorpizzaapp.R


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