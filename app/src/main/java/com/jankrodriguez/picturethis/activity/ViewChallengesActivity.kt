package com.jankrodriguez.picturethis.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.jankrodriguez.picturethis.R
import kotlinx.android.synthetic.main.activity_view_challenges.*

class ViewChallengesActivity : AppCompatActivity() {


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_view_challenges)

    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    goToCreateChallengeBtn.setOnClickListener {
      val intent = Intent(this, CreateChallengeActivity::class.java)
      startActivity(intent)
    }
  }

  private val mOnNavigationItemSelectedListener =
      BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
          R.id.navigation_home -> {
            message.setText(R.string.title_home)
            return@OnNavigationItemSelectedListener true
          }
          R.id.navigation_dashboard -> {
            message.setText(R.string.title_dashboard)
            return@OnNavigationItemSelectedListener true
          }
          R.id.navigation_notifications -> {
            message.setText(R.string.title_notifications)
            return@OnNavigationItemSelectedListener true
          }
        }
        false
      }

}
