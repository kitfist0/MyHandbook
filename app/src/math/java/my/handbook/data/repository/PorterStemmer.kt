package my.handbook.data.repository

import java.util.*
import java.util.regex.Pattern

/**
 * Porter algorithm implementation for Russian language
 */
class PorterStemmer {

    companion object {
        private val PERFECTIVEGROUND = Pattern.compile(
            "((ив|ивши|ившись|ыв|ывши|ывшись)|((<=[ая])(в|вши|вшись)))$"
        )
        private val REFLEXIVE = Pattern.compile("(с[яь])$")
        private val ADJECTIVE = Pattern.compile(
            "(ее|ие|ые|ое|ими|ыми|ей|ий|ый|ой|ем|им|ым|ом|его|ого|ему|ому|их|ых|ую|юю|ая|яя|ою|ею)$"
        )
        private val PARTICIPLE = Pattern.compile("((ивш|ывш|ующ)|((?<=[ая])(ем|нн|вш|ющ|щ)))$")
        private val VERB = Pattern.compile(
            "((ила|ыла|ена|ейте|уйте|ите|или|ыли|ей|уй|ил|ыл|им|ым|ен|ило|ыло|ено|ят|ует|уют|ит|ыт|ены|ить|ыть|ишь|ую|ю)|((?<=[ая])(ла|на|ете|йте|ли|й|л|ем|н|ло|но|ет|ют|ны|ть|ешь|нно)))$"
        )
        private val NOUN = Pattern.compile(
            "(а|ев|ов|ие|ье|е|иями|ями|ами|еи|ии|и|ией|ей|ой|ий|й|иям|ям|ием|ем|ам|ом|о|у|ах|иях|ях|ы|ь|ию|ью|ю|ия|ья|я)$"
        )
        private val RVRE = Pattern.compile("^(.*?[аеиоуыэюя])(.*)$")
        private val DERIVATIONAL = Pattern.compile(".*[^аеиоуыэюя]+[аеиоуыэюя].*ость?$")
        private val DER = Pattern.compile("ость?$")
        private val SUPERLATIVE = Pattern.compile("(ейше|ейш)$")
        private val I = Pattern.compile("и$")
        private val P = Pattern.compile("ь$")
        private val NN = Pattern.compile("нн$")
    }

    fun stem(words: String): String {
        var word = words
        word = word.toLowerCase(Locale.getDefault())
        word = word.replace('ё', 'е')
        val m = RVRE.matcher(word)

        if (m.matches()) {
            val pre = m.group(1) as CharSequence
            var rv = m.group(2) as CharSequence
            var temp = PERFECTIVEGROUND.matcher(rv).replaceFirst("")
            if (temp == rv) {
                rv = REFLEXIVE.matcher(rv).replaceFirst("")
                temp = ADJECTIVE.matcher(rv).replaceFirst("")
                if (temp != rv) {
                    rv = temp
                    rv = PARTICIPLE.matcher(rv).replaceFirst("")
                } else {
                    temp = VERB.matcher(rv).replaceFirst("")
                    rv = if (temp == rv) {
                        NOUN.matcher(rv).replaceFirst("")
                    } else {
                        temp
                    }
                }
            } else {
                rv = temp
            }

            rv = I.matcher(rv).replaceFirst("")

            if (DERIVATIONAL.matcher(rv).matches()) {
                rv = DER.matcher(rv).replaceFirst("")
            }

            temp = P.matcher(rv).replaceFirst("")
            if (temp == rv) {
                rv = SUPERLATIVE.matcher(rv).replaceFirst("")
                rv = NN.matcher(rv).replaceFirst("н")
            } else {
                rv = temp
            }
            word = pre.toString() + rv.toString()
        }

        return word
    }
}
