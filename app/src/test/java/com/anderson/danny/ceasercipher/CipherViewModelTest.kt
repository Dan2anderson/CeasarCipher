package com.anderson.danny.ceasercipher

import org.junit.After
import org.junit.Before
import org.junit.Test

class CipherViewModelTest {
val vm = CipherViewModel()
    @Before
    fun setUp() {
        vm.mode = UiState.EDIT
        vm.setText("")
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getMode() {
        assert(vm.mode == UiState.EDIT)
        vm.mode = UiState.DECODE
        assert(vm.mode == UiState.DECODE)
        vm.mode = UiState.ENCODE
        assert(vm.mode == UiState.ENCODE)
    }

    @Test
    fun setText() {
        val text = "Test1"
        vm.mode= UiState.ENCODE
        vm.setText(text)
        assert(vm.getUserText() != text)
        vm.mode = UiState.DECODE
        vm.setText(text)
        assert(vm.getUserText() != text)

        vm.mode = UiState.EDIT
        vm.setText(text)
        assert(vm.getUserText() == text)
    }

    @Test
    fun setOffsetAndGetModeTextLower() {
        val unencoded = "abc xyz."
        val offsetA = 'a'
        val offsetH  = 'm'
        val encodedH = "mno jkl."
        val offsetZ = 'z'
        val encodedZ = "zab wxy."
        vm.mode = UiState.EDIT
        vm.setText(unencoded)
        vm.mode = UiState.ENCODE
        vm.setOffset(offsetA)
        assert(vm.getModeText() == unencoded)
        vm.setOffset(offsetH)
        assert(vm.getModeText() == encodedH)
        vm.setOffset(offsetZ)
        assert(vm.getModeText() == encodedZ)

    }

    @Test
    fun setOffsetAndGetModeTextUpper() {
        val unencoded = "ABC XYZ."
        val offsetA = 'a'
        val offsetH  = 'm'
        val encodedH = "MNO JKL."
        val offsetZ = 'z'
        val encodedZ = "ZAB WXY."
        vm.mode = UiState.EDIT
        vm.setText(unencoded)
        vm.mode = UiState.ENCODE
        vm.setOffset(offsetA)
        assert(vm.getModeText() == unencoded)
        vm.setOffset(offsetH)
        assert(vm.getModeText() == encodedH)
        vm.setOffset(offsetZ)
        assert(vm.getModeText() == encodedZ)

    }

    @Test
    fun getUserText() {
        val text = "hi there"
        vm.mode = UiState.EDIT
        vm.setText(text)
        assert(vm.getUserText() == text)
    }
}