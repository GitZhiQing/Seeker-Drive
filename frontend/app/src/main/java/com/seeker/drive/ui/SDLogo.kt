package com.seeker.drive.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seeker.drive.R

@Composable
fun SDLogo() {
    Image(
        painter = painterResource(id = R.drawable.tape_drive),
        contentDescription = "Logo",
        modifier = Modifier
            .size(128.dp),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
    )
    Text("Seeker Drive", style = MaterialTheme.typography.headlineSmall)
}