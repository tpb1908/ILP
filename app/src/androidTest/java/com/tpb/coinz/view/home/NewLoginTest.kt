package com.tpb.coinz.view.home


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import com.tpb.coinz.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@LargeTest
@RunWith(AndroidJUnit4::class)
class NewLoginTest {


    /***
     * This test requires that no users are logged in
     *
     */

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(HomeActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION")

    @Test
    fun newLoginTest() {



        val testEmail = "test${Random.nextInt(1000,50000)}@test.com"

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(500)

        val supportVectorDrawablesButton = onView(
                allOf(withId(R.id.email_button), withText("Sign in with email"),
                        childAtPosition(
                                allOf(withId(R.id.btn_holder),
                                        childAtPosition(
                                                withId(R.id.container),
                                                0)),
                                0)))
        supportVectorDrawablesButton.perform(scrollTo(), click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(500)

        val textInputEditText = onView(
                allOf(withId(R.id.email),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.email_layout),
                                        0),
                                0)))
        textInputEditText.perform(scrollTo(), click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(1000)

        val textInputEditText2 = onView(
                allOf(withId(R.id.email),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.email_layout),
                                        0),
                                0)))
        textInputEditText2.perform(scrollTo(), replaceText(testEmail), closeSoftKeyboard())


        val appCompatButton = onView(
                allOf(withId(R.id.button_next), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0),
                                1)))
        appCompatButton.perform(scrollTo(), click())

        val textInputEditText5 = onView(
                allOf(withId(R.id.name),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.name_layout),
                                        0),
                                0)))
        textInputEditText5.perform(scrollTo(), click())

        val textInputEditText6 = onView(
                allOf(withId(R.id.name),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.name_layout),
                                        0),
                                0)))
        textInputEditText6.perform(scrollTo(), replaceText("Test"), closeSoftKeyboard())

        val textInputEditText7 = onView(
                allOf(withId(R.id.password),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.password_layout),
                                        0),
                                0)))
        textInputEditText7.perform(scrollTo(), replaceText("password"), closeSoftKeyboard())


        val appCompatButton2 = onView(
                allOf(withId(R.id.button_create), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0),
                                3)))
        appCompatButton2.perform(scrollTo(), click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(3000)

        val textView = onView(
                allOf(withId(R.id.user_email), withText(testEmail),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.user_info_card),
                                        0),
                                0),
                        isDisplayed()))

        textView.check(matches(withText(testEmail)))

        val textView2 = onView(
                allOf(withId(R.id.map_collection_info), withText("0 coinz collected. 50 remaining"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.user_info_card),
                                        0),
                                1),
                        isDisplayed()))
        textView2.check(matches(withText("0 coinz collected. 50 remaining")))

        val textView3 = onView(
                allOf(withId(R.id.coin_bank_info), withText("0 coinz banked. 25 remaining"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.user_info_card),
                                        0),
                                2),
                        isDisplayed()))
        textView3.check(matches(withText("0 coinz banked. 25 remaining")))
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
