package com.moco.androidspecifics.practice

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.moco.androidspecifics.practice.ui.theme.AndroidSpecificsPracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContent {
            AndroidSpecificsPracticeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MocoLayout(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Preview
    @Composable
    fun MocoLayout(modifier: Modifier = Modifier) {

        // Declares if the additional permission dialog should be shown (on denial)
        var showAdditionalPermissionDialog by remember { mutableStateOf(false) }

        /*
            Defines the launcher for requesting permissions.
            If request result is available,
            set showAdditionalPermissionDialog to corresponding value.
        */
        val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            showAdditionalPermissionDialog = !isGranted
        }

        /*
            Launch permission dialog the first time the app is launched,
            if permission is not already granted.
        */
        LaunchedEffect(key1 = Unit) {
            if (!showAdditionalPermissionDialog) {
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        /* Here the permission dialog is shown */
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
        }

        /*
            Display a Button in the Center of the Screen
         */
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            /*
                Here the button is created.
                The onClick function should create the notification.
             */
            Button(onClick = {
                createNotification()
            }) {
                Text(
                    text = "Create Notification", /*TODO: replace by resource*/
                )
            }
        }

        /*
            Check if the additional permission dialog should be shown.
            This is the case if the user has denied the permission before.
            The user is then asked to grant the permission again.
            If the user denies the permission the second time,
            the user is requested to open the app settings and change the permission,
            because the permission dialog cant be shown anymore.
         */
        if (showAdditionalPermissionDialog) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionDialog(
                    permissionTextProvider = NotificationPermissionTextProvider(),
                    onDismiss = { showAdditionalPermissionDialog = false },
                    onOkClick = {
                        showAdditionalPermissionDialog = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) requestPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    },
                    isPermanentlyDeclined = !shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS),
                    onGoToAppSettingsClick = ::openAppSettings
                )
            }
        }
    }

    /*
        This method should be called when the activity is created.
        It should create the notification channel.
        The Notification Channel has the following properties:
            - It has to have a name and a description
            - It has to have a importance level of IMPORTANCE_HIGH
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* Create and register the notification channel */
            val channel = NotificationChannel(
                /* TODO: Enter a channel id here */,
                /* TODO: Enter the channel name here */,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = /* TODO: Enter the channel description here */

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }

    }

    /*
        This method should be called if the user clicks the button.
        It should build and display a notification.
        The Notification has the following properties:
            - It has to have a title and a text you can choose
            - It has to have a smiley icon (Create a vector resource for the icon)
            - When the user clicks on the notification, it should start the NotificationActivity (use preconfigured pendingIntent)
     */
    private fun createNotification() {
        /*
            Create the intent to start the NotificationActivity.
            The intent is then used to create the PendingIntent.
            The pending intent should be used in setContentIntent() function in the notification builder.
        */
        val intent = Intent(this, NotificationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE)

        /* Build the notification */
        val builder = NotificationCompat(this, /* TODO: Enter the channel id here (from the channel above)*/)
            .setContentTitle(/* TODO: Enter a title for your Notification here */)
            .setContentText(/* TODO: Enter a text for your Notification here */)
            .setSmallIcon(/* TODO: Enter the icon resource for your notification here */)
            .setContentIntent(pendingIntent)

        /*
            The notification is registered here,
            if the permission for sending notification is granted.
            If not, a toast is shown.
         */
        with (NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                /* Register the notification */
                notify(1, builder.build())
            } else
                Toast.makeText(this@MainActivity, "Permission not granted", Toast.LENGTH_SHORT).show()
        }
    }
}

/*
    This Intent is used to open the app settings if the user denies the permission two times.
 */
fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

/*
    TODO: Translate all string resources to german language with translation editor
*/