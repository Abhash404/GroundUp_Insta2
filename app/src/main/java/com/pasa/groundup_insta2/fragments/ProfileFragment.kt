package com.pasa.groundup_insta2.fragments

import android.util.Log
import com.pasa.groundup_insta2.MainActivity
import com.pasa.groundup_insta2.Post
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser

class ProfileFragment: FeedFragment() {

    override fun queryPosts() {

        adapter.clear()
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser())
        query.addDescendingOrder("createdAt")

        query.findInBackground { posts, e ->

            if (e != null) {

                Log.e(MainActivity.TAG, "Action Failed.")

            }

            else {

                if (posts != null) {

                    for (post in posts) {

                        Log.i(MainActivity.TAG, "Post: " + post.getDescription() + " , username: " + post.getUser()?.username)
                    }

                    allPosts.addAll(posts)
                    adapter.notifyDataSetChanged()
                    swipeContainer.isRefreshing = false
                }
            }
        }
    }
}