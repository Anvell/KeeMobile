package io.github.anvell.keemobile.common.mapper

import dagger.Reusable
import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadableException
import io.github.anvell.keemobile.BuildConfig
import io.github.anvell.keemobile.R
import javax.inject.Inject

@Reusable
class IconMapper @Inject constructor() {

    fun map(index: Int): Int {
        return if (index in 0..68) mapTable[index] else mapTable.first()
    }

    companion object {

        private val mapTable = intArrayOf(
            R.drawable.ic_entry_01_key,
            R.drawable.ic_entry_02_earth,
            R.drawable.ic_entry_03_alert,
            R.drawable.ic_entry_04_server,
            R.drawable.ic_entry_05_pin,
            R.drawable.ic_entry_06_chat,
            R.drawable.ic_entry_07_function,
            R.drawable.ic_entry_08_edit_box,
            R.drawable.ic_entry_09_plug,
            R.drawable.ic_entry_10_newspaper,
            R.drawable.ic_entry_11_clip,
            R.drawable.ic_entry_12_camera,
            R.drawable.ic_entry_13_broadcast,
            R.drawable.ic_entry_14_links,
            R.drawable.ic_entry_15_gps,
            R.drawable.ic_entry_16_barcode,
            R.drawable.ic_entry_17_shield_star,
            R.drawable.ic_entry_18_album,
            R.drawable.ic_entry_19_computer,
            R.drawable.ic_entry_20_mail,
            R.drawable.ic_entry_21_settings,
            R.drawable.ic_entry_22_checkbox,
            R.drawable.ic_entry_23_article,
            R.drawable.ic_entry_24_sidebar,
            R.drawable.ic_entry_25_shield_flash,
            R.drawable.ic_entry_26_inbox,
            R.drawable.ic_entry_27_save,
            R.drawable.ic_entry_28_server,
            R.drawable.ic_entry_29_play,
            R.drawable.ic_entry_30_gallery,
            R.drawable.ic_entry_31_terminal,
            R.drawable.ic_entry_32_printer,
            R.drawable.ic_entry_33_grid,
            R.drawable.ic_entry_34_flag,
            R.drawable.ic_entry_35_tools,
            R.drawable.ic_entry_36_laptop,
            R.drawable.ic_entry_37_archive,
            R.drawable.ic_entry_38_card,
            R.drawable.ic_entry_39_windows,
            R.drawable.ic_entry_40_time,
            R.drawable.ic_entry_41_search,
            R.drawable.ic_entry_42_paint,
            R.drawable.ic_entry_43_cpu,
            R.drawable.ic_entry_44_bin,
            R.drawable.ic_entry_45_file,
            R.drawable.ic_entry_46_slash,
            R.drawable.ic_entry_47_question,
            R.drawable.ic_entry_48_cube,
            R.drawable.ic_entry_49_folder,
            R.drawable.ic_entry_50_folder_open,
            R.drawable.ic_entry_51_database,
            R.drawable.ic_entry_52_lock_unlock,
            R.drawable.ic_entry_53_lock,
            R.drawable.ic_entry_54_checkbox,
            R.drawable.ic_entry_55_pencil,
            R.drawable.ic_entry_56_image,
            R.drawable.ic_entry_57_book,
            R.drawable.ic_entry_58_window,
            R.drawable.ic_entry_59_account,
            R.drawable.ic_entry_60_cup,
            R.drawable.ic_entry_61_home,
            R.drawable.ic_entry_62_star,
            R.drawable.ic_entry_63_linux,
            R.drawable.ic_entry_64_quill,
            R.drawable.ic_entry_65_appl,
            R.drawable.ic_entry_66_wp,
            R.drawable.ic_entry_67_usd,
            R.drawable.ic_entry_68_profile,
            R.drawable.ic_entry_69_smartphone
        )
    }
}