package com.mizech.compose_todo

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConfirmAlertDialog(isDelConfirmOpen: MutableState<Boolean>,
                       messageText: String,
                       action: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            isDelConfirmOpen.value = false
        },
        title = {
            Text(text = messageText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold)
        },
        text = {
            Column() {
                Text(
                    stringResource(R.string.conf_del_question),
                    fontSize = 18.sp)
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        action()
                        isDelConfirmOpen.value = false
                    }
                ) {
                    Text(stringResource(R.string.conf_del_yes))
                }
                Button(
                    onClick = {
                        isDelConfirmOpen.value = false
                    }
                ) {
                    Text(stringResource(R.string.conf_del_no))
                }
            }
        }
    )
}