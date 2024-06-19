package com.parasolka.fishingapp5;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RegistrationActivityTest {

    private CountingIdlingResource idlingResource = new CountingIdlingResource("registration");

    @Before
    public void setUp() {
        Espresso.registerIdlingResources(idlingResource);
    }

    @After
    public void tearDown() {
        Espresso.unregisterIdlingResources(idlingResource);
    }

    @Test
    public void testSuccessfulRegistration() {
        try (ActivityScenario<RegistrationActivity> scenario = ActivityScenario.launch(RegistrationActivity.class)) {
            // Заполняем поля
            onView(withId(R.id.et_email)).perform(ViewActions.typeText("test@example.com"), ViewActions.closeSoftKeyboard());
            onView(withId(R.id.et_password)).perform(ViewActions.typeText("123456"), ViewActions.closeSoftKeyboard());

            // Увеличиваем счетчик перед началом асинхронной операции
            idlingResource.increment();

            // Небольшая задержка, чтобы убедиться, что текст введен
            Thread.sleep(1000);

            // Нажимаем на кнопку регистрации
            onView(withId(R.id.buttonRegister)).perform(ViewActions.click());

            // Проверяем успешное создание пользователя
            onView(withText("User registered successfully")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));

            // Уменьшаем счетчик после завершения асинхронной операции
            idlingResource.decrement();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
