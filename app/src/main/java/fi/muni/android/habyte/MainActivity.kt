package fi.muni.android.habyte

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import fi.muni.android.habyte.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    var CALENDAR_PERMISSION = 1;
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navView.menu.getItem(1).isEnabled = false

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.habit_list_fragment, R.id.habyte_list_fragment
            )
        )
        binding.toolbar.setupWithNavController(navController,appBarConfiguration)
        navView.setupWithNavController(navController)
        binding.createHabyteButton.setOnClickListener {
            val int = Intent(this, AddOrUpdateHabyteActivity::class.java)
            startActivity(int)
        }
    }
}