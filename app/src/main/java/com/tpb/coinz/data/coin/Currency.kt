package com.tpb.coinz.data.coin

import androidx.annotation.DrawableRes
import com.tpb.coinz.R

/**
 * Enum representing the in game currencies with drawable resources for their icons
 */
enum class Currency(@DrawableRes val img: Int) {
    PENY(R.drawable.ic_peny), DOLR(R.drawable.ic_dolr), SHIL(R.drawable.ic_shil), QUID(R.drawable.ic_quid);
}