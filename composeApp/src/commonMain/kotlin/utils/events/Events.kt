package utils.events

import getAppMetricaKMP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

object Events {
    const val APP_OPEN = "открытие_приложения"
    const val SCREEN_1_NEXT = "экран_онбординг_далее"
    const val SCREEN_1_TO_AUTH = "экран_онбординг_переход_к_авторизации"
    const val SCREEN_2_AUTH_VK = "экран_авторизации_нажата_кнопка_вк"
    const val SCREEN_2_AUTH_GOOGLE = "экран_авторизации_нажата_кнопка_гугл"
    const val SCREEN_2_AUTH_APPLE = "экран_авторизации_нажата_кнопка_эпл"
    const val SCREEN_3_TO_ANIMATE_PHOTO = "экран_список_фильтров_выбран_оживить_фото"
    const val SCREEN_3_TO_BEFORE_AFTER = "экран_список_фильтров_выбран_до_и_после"
    const val SCREEN_3_TO_VIDEO_AVATAR = "экран_список_фильтров_выбран_видеоаватар"
    const val SCREEN_3_TO_VIDEO_PROMPT = "экран_список_фильтров_выбран_промт"
    const val SCREEN_3_TO_SUB = "экран_список_фильтров_переход_к_подпискам"
    const val SCREEN_3_TO_FILTER = "экран_список_фильтров_выбран_фильтр"
    const val SCREEN_4_FILTER_BACK = "экран_редактирования_фильтр_назад"
    const val SCREEN_4_FILTER_CLOSE = "экран_редактирования_фильтр_закрыть"
    const val SCREEN_4_FILTER_PICK_IMAGE = "экран_редактирования_фильтр_выбрано_фото"
    const val SCREEN_4_FILTER_GEN = "экран_редактирования_фильтр_нажата_генерация"
    const val SCREEN_4_PROMT_GEN = "экран_редактирования_промт_нажата_генерация"
    const val SCREEN_4_PROMT_BACK = "экран_редактирования_промт_назад"
    const val SCREEN_4_PROMT_CLOSE = "экран_редактирования_промт_закрыть"
    const val SCREEN_4_CREATION_BACK = "экран_редактирования_творчество_назад"
    const val SCREEN_4_CREATION_CLOSE = "экран_редактирования_творчество_закрыть"
    const val SCREEN_4_CREATION_GEN = "экран_редактирования_творчество_нажата_генерация"
    const val SCREEN_4_CREATION_PICK_FILTER = "экран_редактирования_творчество_выбран_фильтр"
    const val SCREEN_4_CREATION_PICK_AVATAR = "экран_редактирования_творчество_выбран_аватар"
    const val SCREEN_4_EDITOR_BACK = "экран_редактирования_редактор_назад"
    const val SCREEN_4_EDITOR_CLOSE = "экран_редактирования_редактор_закрыть"
    const val SCREEN_4_EDITOR_GEN = "экран_редактирования_редактор_нажата_генерация"
    const val SCREEN_4_EDITOR_PICK_IMAGE = "экран_редактирования_редактор_выбрано_фото"
    const val SCREEN_5_HISTORY_TO_SUB = "экран_истории_переход_к_подпискам"
    const val SCREEN_5_HISTORY_GEN = "экран_истории_переход_к_генерации"
    const val SCREEN_5_HISTORY_CLICK_IMAGE = "экран_истории_выбрано_изображение"
    const val SCREEN_6_RESULT_CLOSE = "экран_результата_закрыть"
    const val SCREEN_6_RESULT_SHARE_VK = "экран_результата_поделиться_vk"
    const val SCREEN_6_RESULT_SHARE_MAX = "экран_результата_поделиться_max"
    const val SCREEN_6_RESULT_SHARE_WHATSAPP = "экран_результата_поделиться_whatsapp"
    const val SCREEN_6_RESULT_SHARE_TELEGRAM = "экран_результата_поделиться_telegram"
    const val SCREEN_6_RESULT_SHARE_INSTAGRAM = "экран_результата_поделиться_instagram"
    const val SCREEN_7_SETTINGS_TO_SUB = "экран_настроек_переход_к_подпискам"
    const val SCREEN_7_SETTINGS_REQUEST_FUNC = "экран_настроек_запросить_функцию"
    const val SCREEN_7_SETTINGS_SHARE_APP = "экран_настроек_поделиться_приложением"
    const val SCREEN_7_SETTINGS_REVIEW = "экран_настроек_оценить_нас"
    const val SCREEN_7_SETTINGS_PRIVACY_POLICY = "экран_настроек_политика_конфиденциальности"
    const val SCREEN_7_SETTINGS_TERMS_OF_USE = "экран_настроек_условия_использования"
    const val SCREEN_7_SETTINGS_LOGOUT = "экран_настроек_выход_из_профиля"
    const val SCREEN_7_SETTINGS_DELETE_ACCOUNT = "экран_настроек_удаление_аккаунта"
    const val SCREEN_8_REQUEST_FUNC_SEND = "экран_запроса_функции_отправить"
    const val SCREEN_8_REQUEST_BACK = "экран_запроса_функции_назад"
    const val SCREEN_9_PAYWALL_BACK = "экран_подписок_назад"
    const val SCREEN_9_PAYWALL_SUB_MONTH = "экран_подписок_выбран_месяц"
    const val SCREEN_9_PAYWALL_SUB_YEAR = "экран_подписок_выбран_год"
    const val SCREEN_9_PAYWALL_BUY_1000_TOKENS = "экран_подписок_выбраны_1000_токенов"
    const val SCREEN_9_PAYWALL_BUY_2000_TOKENS = "экран_подписок_выбраны_2000_токенов"
    const val SCREEN_9_PAYWALL_1_MONTH_VK = "экран_подписок_выбран_план_1_месяц_вк"
    const val SCREEN_9_PAYWALL_12_MONTH_VK = "экран_подписок_выбран_план_12_месяцев_вк"
    const val PURCHASED_SUB_RUSTORE_MONTH = "куплена_подписка_rustore_месяц"
    const val PURCHASED_SUB_RUSTORE_YEAR = "куплена_подписка_rustore_год"
    const val PURCHASED_1000_TOKENS_RUSTORE = "куплено_1000_токенов_rustore"
    const val PURCHASED_2000_TOKENS_RUSTORE = "куплено_2000_токенов_rustore"
    const val SELECTED_SUB_RUSTORE_MONTH = "выбрано_подписка_rustore_месяц"
    const val SELECTED_SUB_RUSTORE_YEAR = "выбрано_подписка_rustore_год"
    const val SELECTED_1000_TOKENS_RUSTORE = "выбрано_1000_токенов_rustore"
    const val SELECTED_2000_TOKENS_RUSTORE = "выбрано_2000_токенов_rustore"
    const val PURCHASED_400_TOKENS_RUSTORE = "куплено_400_токенов_rustore"
    const val PURCHASED_1600_TOKENS_RUSTORE = "куплено_1600_токенов_rustore"
    const val PURCHASED_2600_TOKENS_RUSTORE = "куплено_2600_токенов_rustore"
    const val PURCHASED_5000_TOKENS_RUSTORE = "куплено_5000_токенов_rustore"
    const val PURCHASED_8000_TOKENS_RUSTORE = "куплено_8000_токенов_rustore"
    const val PURCHASED_14000_TOKENS_RUSTORE = "куплено_14000_токенов_rustore"
    const val PURCHASED_20000_TOKENS_RUSTORE = "куплено_20000_токенов_rustore"
    const val PURCHASED_26000_TOKENS_RUSTORE = "куплено_26000_токенов_rustore"
    const val SELECTED_400_TOKENS_RUSTORE = "выбрано_400_токенов_rustore"
    const val SELECTED_1600_TOKENS_RUSTORE = "выбрано_1600_токенов_rustore"
    const val SELECTED_2600_TOKENS_RUSTORE = "выбрано_2600_токенов_rustore"
    const val SELECTED_5000_TOKENS_RUSTORE = "выбрано_5000_токенов_rustore"
    const val SELECTED_8000_TOKENS_RUSTORE = "выбрано_8000_токенов_rustore"
    const val SELECTED_14000_TOKENS_RUSTORE = "выбрано_14000_токенов_rustore"
    const val SELECTED_20000_TOKENS_RUSTORE = "выбрано_20000_токенов_rustore"
    const val SELECTED_26000_TOKENS_RUSTORE = "выбрано_26000_токенов_rustore"
    const val PURCHASED_SUB_GOOGLE_MONTH = "куплена_подписка_google_месяц"
    const val PURCHASED_SUB_GOOGLE_YEAR = "куплена_подписка_google_год"
    const val PURCHASED_1000_TOKENS_GOOGLE = "куплено_1000_токенов_google"
    const val PURCHASED_2000_TOKENS_GOOGLE = "куплено_2000_токенов_google"
    const val PURCHASED_SUB_REVCAT_MONTH = "куплена_подписка_revcat_месяц"
    const val PURCHASED_SUB_REVCAT_YEAR = "куплена_подписка_revcat_год"
    const val PURCHASED_1000_TOKENS_REVCAT = "куплено_1000_токенов_revcat"
    const val PURCHASED_2000_TOKENS_REVCAT = "куплено_2000_токенов_revcat"
    const val NAV_TO_LIST_SCREEN = "переход_на_экран_списка_фильтров"
    const val NAV_TO_HISTORY_SCREEN = "переход_на_экран_истории"
    const val NAV_TO_SETTINGS_SCREEN = "переход_на_экран_настроек"
    const val INIT_SUB_VK_DONUT = "активирована_подписка_вк_донат"

    fun put(event: String) {
        CoroutineScope(Dispatchers.IO).launch {
            getAppMetricaKMP()?.reportEvent(event)
            //getFirebaseKMP().reportEvent(event)
        }
    }

    fun put(event: String, parameters: Map<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            getAppMetricaKMP()?.reportEvent(event, parameters)
            //getFirebaseKMP().reportEvent(event, parameters)
        }
    }
}