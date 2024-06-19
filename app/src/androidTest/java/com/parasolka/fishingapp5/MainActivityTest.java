package com.parasolka.fishingapp5;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Test
    public void testSuccessfulSignIn() {
        // Запускаем активити MainActivity
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            // Вводим данные для входа
            onView(withId(R.id.et_email)).perform(ViewActions.replaceText("2@gmail.com"));
            onView(withId(R.id.et_password)).perform(ViewActions.replaceText("123123"));

            // Жмем кнопку Войти
            onView(withId(R.id.btn_sign_in)).perform(ViewActions.click());

            // Ждем выполнения входа
            Thread.sleep(5000); // Подождем 5 секунд для завершения операции (лучше заменить на ожидание по условию)

            // Проверяем, что текст "Sign in successful." появляется на экране
            onView(withText("Sign in successful.")).check(matches(isDisplayed()));

            // Проверяем, что пользователь авторизован (опционально)
            FirebaseAuth auth = FirebaseAuth.getInstance();
            assert auth.getCurrentUser() != null; // Проверяем, что текущий пользователь не равен null

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
