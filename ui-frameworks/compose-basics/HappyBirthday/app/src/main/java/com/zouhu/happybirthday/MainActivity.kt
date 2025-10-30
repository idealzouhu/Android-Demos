package com.zouhu.happybirthday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zouhu.happybirthday.ui.theme.HappyBirthdayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HappyBirthdayTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // GreetingText(
                    //     message = "Happy Birthday Sam!",
                    //     from = "From Emma",
                    //     modifier = Modifier.padding(8.dp) // 添加内边距
                    // )
                    GreetingImage(
                        // 使用硬编码字符串
                        message = "Happy Birthday Sam!",
                        from = "From Emma"

                        // 使用字符串资源
                        // message = stringResource(R.string.happy_birthday_text),
                        // from = stringResource(R.string.signature_text)
                    )
                }
            }
        }
    }
}

/**
 * 显示问候语的可组合函数
 *
 * 此函数在一个垂直列中显示两条文本信息：问候语和来源问候语的发送者信息
 * 它使用Jetpack Compose的组件来布局和样式化这些文本信息
 *
 * @param message 问候语的文本内容
 * @param from 问候语来源的发送者信息
 * @param modifier 可选的Modifier，用于自定义此组件的布局属性
 */
@Composable
fun GreetingText(message: String, from: String, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center, // 垂直居中
        modifier = modifier
    ) {
        Text(
            text = message,
            fontSize = 100.sp,
            lineHeight = 136.sp,
            textAlign = TextAlign.Center // 文本居中
        )
        Text(
            text = from,
            fontSize = 36.sp,
            modifier = Modifier
                .padding(16.dp)
                .align(alignment = Alignment.End)
        )
    }
}

/**
 * 显示带有背景图片的问候语
 *
 * 本函数组合了一个带有背景图片和前景问候文本的UI组件它使用了Jetpack Compose的组合式UI构建方式
 * 通过调整图片的透明度，使得问候语能够清晰地显示在图片之上
 *
 * @param message 问候语的文本内容
 * @param from 问候语的来源或发送者
 * @param modifier 可选的修饰符，用于自定义组件的外观和布局行为，默认为Modifier
 */
@Composable
fun GreetingImage(message: String, from: String, modifier: Modifier = Modifier) {
    // 获取图片资源
    val image = painterResource(R.drawable.androidparty)

    // 创建一个盒容器，用于叠加图片和文本
    Box(modifier) {
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop, // 调整图片大小，以使其全屏显示
            alpha = 0.5F    // 透明度
        )
        GreetingText(
            message = message,
            from = from,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BirthdayCardPreview() {
    HappyBirthdayTheme {
        // GreetingText(message = "Happy Birthday Sam!", from = "From Emma")
        GreetingImage(
            message = "Happy Birthday Sam!",
            from = "From Emma"
        )
    }
}
