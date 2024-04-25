package com.prplmnstr.drops.utils

import java.util.Arrays

object Constants {
    @JvmField
    var PRESENT = 1
    @JvmField
    var ABSENT = 0
    @JvmField
    var NO_ATTENDANCE = 2
    @JvmField
    var ATTENDANCE = "Attendances"
    @JvmField
    var PLANT_REPORTS = "PlantReports"
    var OUTLETS = "Outlets"
    @JvmField
    var PLANTS = "Plants"
    var NAME = "name"
    var DATES = "dates"
    @JvmField
    var RECHARGE_UNIT = "Recharge Unit"
    @JvmField
    var STATIONARY_UNIT = "Stationary Unit"
    @JvmField
    var MOBILE_UNIT = "Mobile Unit"
    @JvmField
    var WORKER = "Worker"
    @JvmField
    var INVESTOR = "Investor"
    @JvmField
    var EXPENSES = "Expenses"
    @JvmField
    var ADMIN = "Admin"
    @JvmField
    var PLANT_NAME = "plantName"
    @JvmField
    var UNIT_NAME = "unitName"
    @JvmField
    var UNIT_TYPE_STRING = "unitType"
    @JvmField
    var RECORDS = "Records"
    @JvmField
    var PLANT_IMAGE = "image"
    @JvmField
    var ADMIN_MAIL = "admin@gmail.com"
    var USERS = "Users"
    @JvmField
    var SHARED_PREFERENCE = "Myprefs"
    @JvmField
    var SAVED_USER_TYPE = "userType"
    @JvmField
    var PICK_IMAGE_REQUEST_CODE = 123
    @JvmField
    var CHANGE_IMAGE_REQUEST_CODE = 111
    @JvmField
    var UNIT_TYPE = Arrays.asList(STATIONARY_UNIT, MOBILE_UNIT, RECHARGE_UNIT)
    @JvmField
    var USER_TYPE = Arrays.asList(WORKER, INVESTOR)
    @JvmField
    var USER_TYPE_SIGN_IN = Arrays.asList(WORKER, INVESTOR, ADMIN)
}
