package com.jankrodriguez.picturethis.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jankrodriguez.picturethis.R

class CreateChallengeActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_create_challenge)

		supportActionBar?.setTitle(R.string.create_challenge_title)
	}
}
