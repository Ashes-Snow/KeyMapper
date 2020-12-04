package io.github.sds100.keymapper.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import io.github.sds100.keymapper.data.model.Action
import io.github.sds100.keymapper.data.model.options.ActionShortcutOptions
import io.github.sds100.keymapper.data.repository.DeviceInfoRepository
import io.github.sds100.keymapper.util.EnableAccessibilityServicePrompt
import io.github.sds100.keymapper.util.FixFailure
import io.github.sds100.keymapper.util.SealedEvent

/**
 * Created by sds100 on 08/09/20.
 */
class CreateActionShortcutViewModel(deviceInfoRepository: DeviceInfoRepository) : ViewModel() {

    val actionListViewModel = object : ActionListViewModel<ActionShortcutOptions>(
        viewModelScope,
        deviceInfoRepository
    ) {
        override fun getActionOptions(action: Action): ActionShortcutOptions {
            return ActionShortcutOptions(action, actionList.value!!.size)
        }
    }

    private val _eventStream = LiveEvent<SealedEvent>().apply {
        addSource(actionListViewModel.eventStream) {
            when (it) {
                is FixFailure, is EnableAccessibilityServicePrompt -> value = it
            }
        }
    }

    val eventStream: LiveData<SealedEvent> = _eventStream

    class Factory(private val mDeviceInfoRepository: DeviceInfoRepository) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>) =
            CreateActionShortcutViewModel(mDeviceInfoRepository) as T
    }
}