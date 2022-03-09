package com.application.bmiantiobesity.ui.main

import com.application.bmiantiobesity.db.usersettings.ConfigToDisplay

interface EditSheetListener {
    fun onDataSelected(items: List<ConfigToDisplay>)
}