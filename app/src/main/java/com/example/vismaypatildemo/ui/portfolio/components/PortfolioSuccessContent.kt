package com.example.vismaypatildemo.ui.portfolio.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vismaypatildemo.R
import com.example.vismaypatildemo.domain.model.Holding
import com.example.vismaypatildemo.domain.model.PortfolioSummary
import com.example.vismaypatildemo.domain.usecase.CalculatePortfolioSummaryUseCase

@Composable
fun PortfolioSuccessContent(
    portfolio: PortfolioSummary,
    isSummaryExpanded: Boolean,
    onToggleSummary: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = if (isSummaryExpanded) 280.dp else 180.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.portfolio_holdings),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(
                items = portfolio.holdings,
                key = { it.symbol }
            ) { holding ->
                HoldingItem(holding = holding)
            }
        }

        PortfolioSummaryCard(
            portfolio = portfolio,
            isExpanded = isSummaryExpanded,
            onToggle = onToggleSummary,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun PortfolioSuccessContentPreview() {
    val sampleHoldings = listOf(
        Holding(
            symbol = "AAPL",
            quantity = 10,
            ltp = 150.0,
            avgPrice = 140.0,
            close = 148.0
        ),
        Holding(
            symbol = "GOOGL",
            quantity = 5,
            ltp = 2800.0,
            avgPrice = 2700.0,
            close = 2790.0
        ),
        Holding(
            symbol = "TSLA",
            quantity = 8,
            ltp = 800.0,
            avgPrice = 750.0,
            close = 795.0
        )
    )

    val portfolio = CalculatePortfolioSummaryUseCase()(sampleHoldings)

    MaterialTheme {
        PortfolioSuccessContent(
            portfolio = portfolio,
            isSummaryExpanded = false,
            onToggleSummary = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun PortfolioSuccessContentExpandedPreview() {
    val sampleHoldings = listOf(
        Holding(
            symbol = "AAPL",
            quantity = 10,
            ltp = 150.0,
            avgPrice = 140.0,
            close = 148.0
        ),
        Holding(
            symbol = "GOOGL",
            quantity = 5,
            ltp = 2800.0,
            avgPrice = 2700.0,
            close = 2790.0
        )
    )

    val portfolio = CalculatePortfolioSummaryUseCase()(sampleHoldings)

    MaterialTheme {
        PortfolioSuccessContent(
            portfolio = portfolio,
            isSummaryExpanded = true,
            onToggleSummary = {}
        )
    }
}