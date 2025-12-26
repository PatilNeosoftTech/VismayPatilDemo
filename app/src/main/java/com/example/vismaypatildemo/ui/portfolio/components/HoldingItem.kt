package com.example.vismaypatildemo.ui.portfolio.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vismaypatildemo.R
import com.example.vismaypatildemo.domain.model.Holding
import com.example.vismaypatildemo.ui.theme.Green
import com.example.vismaypatildemo.ui.theme.Red
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HoldingItem(
    holding: Holding,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = holding.symbol,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.holding_quantity_format, holding.quantity),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(
                            R.string.holding_ltp_format,
                            holding.ltp.formatCurrency()
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    PnLChip(
                        pnl = holding.totalPnL,
                        percentage = holding.pnLPercentage
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailItem(
                    label = stringResource(R.string.holding_current_value),
                    value = holding.currentValue.formatCurrency()
                )
                DetailItem(
                    label = stringResource(R.string.holding_investment),
                    value = holding.totalInvestment.formatCurrency()
                )
                DetailItem(
                    label = stringResource(R.string.holding_today_pnl),
                    value = holding.todaysPnL.formatCurrency(),
                    valueColor = if (holding.todaysPnL >= 0) Green else Red
                )
            }
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

@Composable
private fun PnLChip(
    pnl: Double,
    percentage: Double
) {
    val isProfit = pnl >= 0
    val backgroundColor = if (isProfit)
        Green.copy(alpha = 0.1f) else Red.copy(alpha = 0.1f)
    val textColor = if (isProfit) Green else Red

    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "${if (isProfit) "+" else ""}${pnl.formatCurrency()} (${percentage.formatPercentage()}%)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
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