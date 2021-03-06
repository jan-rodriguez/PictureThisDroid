package com.jankrodriguez.picturethis.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.GoogleApiClient
import com.jankrodriguez.picturethis.R
import com.jankrodriguez.picturethis.model.CreateUserBody
import com.jankrodriguez.picturethis.service.PICTURE_THIS_SERVICE_INSTANCE
import com.jankrodriguez.picturethis.sharedprefs.getUser
import com.jankrodriguez.picturethis.sharedprefs.getUserSharedPreferences
import com.jankrodriguez.picturethis.sharedprefs.saveUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*


private const val TAG = "LoginActivity"
private const val RC_SIGN_IN = 9001

class LoginActivity : FragmentActivity() {

  private var mGoogleApiClient: GoogleApiClient? = null
  private var userSharedPrefs: SharedPreferences? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    userSharedPrefs = getUserSharedPreferences()

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestProfile()
        .requestEmail()
        .build()

    mGoogleApiClient = GoogleApiClient.Builder(this)
        .enableAutoManage(this, {
          Log.d(TAG, "onConnectionFailed:$it")
        })
        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
        .build()

    googleSignInBtn.setOnClickListener {
      signIn()
    }
  }

  private fun signIn() {
    val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
    startActivityForResult(signInIntent, RC_SIGN_IN)
  }

  // [START onActivityResult]
  public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == RC_SIGN_IN) {
      val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
      handleSignInResult(result)
    }
  }
  // [END onActivityResult]


  public override fun onStart() {
    super.onStart()

    val opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient)
    if (opr.isDone) {
      // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
      // and the GoogleSignInResult will be available instantly.
      Log.d(TAG, "Got cached sign-in")
      val result = opr.get()
      handleSignInResult(result)
    } else {
      // If the user has not previously signed in on this device or the sign-in has expired,
      // this asynchronous branch will attempt to sign in the user silently.  Cross-device
      // single sign-on will occur in this branch.
      opr.setResultCallback { googleSignInResult ->
        handleSignInResult(googleSignInResult)
      }
    }
  }

  // [START handleSignInResult]
  private fun handleSignInResult(result: GoogleSignInResult) {
    Log.d(TAG, "handleSignInResult:" + result.isSuccess)
    if (result.isSuccess) {
      // Signed in successfully, show authenticated UI.
      val acct = result.signInAccount
      Log.d(TAG, acct?.id)

      val name = acct?.displayName
      val googId = acct?.id

      if (name != null && googId != null) {
        if (userSharedPrefs?.getUser() != null) {
          // TODO: Should validate that this user still exists on the back-end
          goToViewChallenges()
        } else {
          // Don't have user, either create or get
          PICTURE_THIS_SERVICE_INSTANCE.createUser(
              CreateUserBody(
                  name = name,
                  google_id = googId))
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(
                  {
                    userSharedPrefs?.saveUser(it)
                    // Go to view challenges
                    goToViewChallenges()
                  },
                  { Log.e(TAG, it.message) })
        }
      }
    } else {
      // Signed out, show unauthenticated UI.
//            updateUI(false)
      Log.d(TAG, result.status.zzvv())
    }
  }
  // [END handleSignInResult]

  private fun goToViewChallenges() {
    // Go to view challenges
    val intent = Intent(this, ViewChallengesActivity::class.java)
    startActivity(intent)
    finish()
  }
}
