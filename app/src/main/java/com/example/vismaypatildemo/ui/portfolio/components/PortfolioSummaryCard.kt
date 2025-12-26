package com.example.vismaypatildemo.ui.portfolio.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vismaypatildemo.R
import com.example.vismaypatildemo.domain.model.PortfolioSummary
import com.example.vismaypatildemo.ui.theme.Green
import com.example.vismaypatildemo.ui.theme.Red
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PortfolioSummaryCard(
    portfolio: PortfolioSummary,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "arrow_rotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.portfolio_profit_loss),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) stringResource(R.string.all_collapse)
                    else stringResource(R.string.all_expand),
                    modifier = Modifier.rotate(rotationAngle),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            CollapsedSummary(portfolio = portfolio)

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                            alpha = 0.2f
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ExpandedSummary(portfolio = portfolio)
                }
            }
        }
    }
}

@Composable
private fun CollapsedSummary(portfolio: PortfolioSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SummaryItem(
            label = stringResource(R.string.portfolio_current_value),
            value = portfolio.currentValue.formatCurrency(),
            valueStyle = MaterialTheme.typography.titleLarge
        )

        SummaryItem(
            label = stringResource(R.string.portfolio_total_investment),
            value = portfolio.totalInvestment.formatCurrency(),
            valueStyle = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun ExpandedSummary(portfolio: PortfolioSummary) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.portfolio_today_profit_loss),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            PnLValue(
                value = portfolio.todaysPnL,
                showBackground = true
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.portfolio_profit_loss),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Column(horizontalAlignment = Alignment.End) {
                PnLValue(
                    value = portfolio.totalPnL,
                    showBackground = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${portfolio.totalPnLPercentage.formatPercentage()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (portfolio.isInProfit)
                        Green else Red
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    valueStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleMedium
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = valueStyle,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun PnLValue(
    value: Double,
    showBackground: Boolean = false
) {
    val isProfit = value >= 0
    val backgroundColor = if (isProfit)
        Green.copy(alpha = 0.2f) else Red.copy(alpha = 0.2f)
    val textColor = if (isProfit) Green else Red

    val modifier = if (showBackground) {
        Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    } else {
        Modifier
    }

    Text(
        text = "${if (isProfit) "+" else ""}${value.formatCurrency()}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = textColor,
        modifier = modifier
    )
}

private fun Double.formatCurrency(): String {
    val indiaLocale = Locale.Builder().setLanguage("en").setRegion("IN").build()
    val format = NumberFormat.getCurrencyInstance(indiaLocale)
    format.maximumFractionDigits = 2
    return format.format(this)
}

private fun Double.formatPercentage(): String {
    return String.format("%.2f", this)
}