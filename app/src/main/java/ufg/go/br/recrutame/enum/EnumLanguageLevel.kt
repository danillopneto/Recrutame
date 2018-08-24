package ufg.go.br.recrutame.enum

import ufg.go.br.recrutame.R

enum class EnumLanguageLevel(val idString: Int) {
    ELEMENTARY(R.string.elementary),
    LIMITED_WORKING(R.string.limited_working),
    PROFESSIONAL_WORKING(R.string.professional_working),
    FULL_PROFESSIONAL(R.string.full_professional),
    NATIVE_OR_BILINGUAL(R.string.native_or_bilingual)
}