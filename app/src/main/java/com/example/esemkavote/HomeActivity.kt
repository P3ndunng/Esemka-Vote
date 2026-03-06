package com.example.esemkavote

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.esemkavote.fragment.FragmentHome
import com.example.esemkavote.fragment.FragmentVote

class HomeActivity : AppCompatActivity() {

    companion object {
        const val EVENT_ID = "EVENT_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (savedInstanceState == null) {
            loadFragment(FragmentHome())
        }
    }

    fun loadFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }

    fun goToVoting(eventId: Int) {
        val votingFragment = FragmentVote()

        val bundle = Bundle()
        bundle.putInt(EVENT_ID, eventId)

        votingFragment.arguments = bundle

        loadFragment(votingFragment, true)
    }

    fun backToHomeAndRefresh() {

        supportFragmentManager.popBackStack()

        supportFragmentManager.executePendingTransactions()

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (fragment is FragmentHome) {
            fragment.loadEvents()
        }
    }
}