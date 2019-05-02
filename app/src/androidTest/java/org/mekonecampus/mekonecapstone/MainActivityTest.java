package org.mekonecampus.mekonecapstone;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("org.mekonecampus.mekonecapstone", appContext.getPackageName());
    }

    @Test
    public void testImageDisplay() {
        onView(withId(R.id.profileImageView)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddButtonClick() {
        onView(withId(R.id.undoBtn)).perform(click());
        onView(withId(R.id.rejectBtn)).perform(click());
        onView(withId(R.id.acceptBtn)).perform(click());
        onView(withId(R.id.editNotes)).check(matches(withText("")));
        onView(withId(R.id.homeBtn)).perform(click());
    }
}
