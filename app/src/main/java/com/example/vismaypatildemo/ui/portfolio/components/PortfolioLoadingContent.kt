package com.example.vismaypatildemo.ui.portfolio.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vismaypatildemo.R

@Composable
fun PortfolioLoadingContent(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.loading_portfolio)
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PortfolioLoadingContentPreview() {
    MaterialTheme {
        PortfolioLoadingContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun PortfolioLoadingContentCustomMessagePreview() {
    MaterialTheme {
        PortfolioLoadingContent(message = stringResource(R.string.loading_fetching_investments))
    }
}