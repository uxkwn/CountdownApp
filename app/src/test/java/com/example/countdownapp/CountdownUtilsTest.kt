package com.example.countdownapp

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class CountdownUtilsTest {

    @Test
    fun calculateDaysRemaining_futureDate_returnsCorrectDays() {
        val current = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 18, 10, 0, 0)
        }.timeInMillis

        val target = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 28, 15, 30, 0)
        }.timeInMillis

        val days = CountdownUtils.calculateDaysRemaining(target, current)
        assertEquals(10L, days)
    }

    @Test
    fun calculateDaysRemaining_tomorrow_returnsOneDay() {
        val current = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 18, 23, 59, 0)
        }.timeInMillis

        val target = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 19, 0, 1, 0)
        }.timeInMillis

        val days = CountdownUtils.calculateDaysRemaining(target, current)
        assertEquals(1L, days)
    }

    @Test
    fun calculateDaysRemaining_sameDay_returnsZero() {
        val current = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 18, 8, 0, 0)
        }.timeInMillis

        val target = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 18, 20, 0, 0)
        }.timeInMillis

        val days = CountdownUtils.calculateDaysRemaining(target, current)
        assertEquals(0L, days)
    }

    @Test
    fun calculateDaysRemaining_pastDate_returnsNegativeDays() {
        val current = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 18, 10, 0, 0)
        }.timeInMillis

        val target = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 15, 10, 0, 0)
        }.timeInMillis

        val days = CountdownUtils.calculateDaysRemaining(target, current)
        assertEquals(-3L, days)
    }

    @Test
    fun formatCountdownText_formattingRules() {
        assertEquals("5 days till Vacation", CountdownUtils.formatCountdownText("Vacation", 5))
        assertEquals("1 day till Graduation", CountdownUtils.formatCountdownText("Graduation", 1))
        assertEquals("Birthday is today!", CountdownUtils.formatCountdownText("Birthday", 0))
        assertEquals("1 day since Concert", CountdownUtils.formatCountdownText("Concert", -1))
        assertEquals("3 days since Trip", CountdownUtils.formatCountdownText("Trip", -3))
    }
}
