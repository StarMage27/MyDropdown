package io.github.starmage27.mydropdown

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.NEXUS_5
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.starmage27.mydropdown.ui.theme.MyDropdownTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDropdownTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

data class OptionsForDropdown(
    var selected: String = "Option 1",
    var expanded: Boolean = false,
    val options: List<String> = listOf("Option 1", "Opt 2", "OptionOption 3", "a", "aaaaaaaaaaa")
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    val states = remember { mutableStateListOf(false, false, false, false) }
    val options = remember {
        mutableStateListOf(
            OptionsForDropdown(),
            OptionsForDropdown(),
            OptionsForDropdown(),
            OptionsForDropdown(),
        )
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyRow(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                val rModifier = Modifier.padding(horizontal = 4.dp)

                itemsIndexed(options.toList()) { index, option ->
                    MyDropdown(
                        modifier = rModifier,
                        selected = option.selected,
                        options = option.options,
                        expanded = states[index],
                        onValueChange = {
                            options[index].selected = it
                        },
                        onExpandedChange = {
                            states[index] = it
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }

            Text(
                modifier = Modifier.padding(8.dp),
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, device = NEXUS_5)
@Composable
fun GreetingPreview() {
    MyDropdownTheme {
        HomeScreen()
    }
}