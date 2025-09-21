
interface Character {
    val name: String
    var health: Int
    var level: Int
    val maxHealth: Int
    fun attack(): Int
    fun takeDamage(damage: Int)
    fun isAlive(): Boolean = health > 0
    fun displayStatus()
}

interface Combatant {
    fun performSpecialAttack(): Int
    fun defend(): Int
}

interface MagicUser {
    var mana: Int
    val maxMana: Int
    fun castSpell(spellName: String): Int
    fun restoreMana(amount: Int)
}


abstract class BaseCharacter(
    override val name: String,
    override var health: Int,
    override var level: Int = 1
) : Character {
    override val maxHealth: Int = health

    override fun takeDamage(damage: Int) {
        val reducedDamage = (damage - level).coerceAtLeast(0)
        health = (health - reducedDamage).coerceAtLeast(0)
        println("$name takes $reducedDamage damage. HP: $health/$maxHealth")
    }

    override fun displayStatus() {
        println("[$name] (Lvl $level) HP: $health/$maxHealth")
    }

    protected fun calculateBaseDamage(): Int = 10 + level * 2
}


class Warrior(name: String, health: Int) : BaseCharacter(name, health), Combatant {
    override fun attack(): Int {
        val dmg = calculateBaseDamage()
        println("$name slashes for $dmg damage!")
        return dmg
    }

    override fun performSpecialAttack(): Int {
        val dmg = calculateBaseDamage() + 15
        println("$name performs Mighty Strike for $dmg damage!")
        return dmg
    }

    override fun defend(): Int {
        val reduction = 5 + level
        println("$name blocks, reducing $reduction damage!")
        return reduction
    }
}

class Mage(name: String, health: Int, override var mana: Int) :
    BaseCharacter(name, health), MagicUser, Combatant {

    override val maxMana: Int = mana

    override fun attack(): Int {
        val dmg = 5 + level
        println("$name hits with staff for $dmg damage!")
        return dmg
    }

    override fun castSpell(spellName: String): Int {
        return when (spellName) {
            "Fireball" -> if (mana >= 20) { mana -= 20; println("$name casts Fireball!"); 25 + level * 2 } else 0
            "Lightning" -> if (mana >= 25) { mana -= 25; println("$name casts Lightning!"); 30 + level * 3 } else 0
            "Heal" -> if (mana >= 15) { mana -= 15; health = (health + 20).coerceAtMost(maxHealth); println("$name heals! 💚"); 0 } else 0
            else -> 0
        }
    }

    override fun restoreMana(amount: Int) {
        mana = (mana + amount).coerceAtMost(maxMana)
        println("$name restores $amount mana. Mana: $mana/$maxMana")
    }

    override fun performSpecialAttack(): Int {
        return if (mana >= 30) {
            mana -= 30
            val dmg = calculateBaseDamage() + 20
            println("$name unleashes Arcane Blast for $dmg damage!")
            dmg
        } else 0
    }

    override fun defend(): Int {
        val reduction = 4 + level
        println("$name conjures barrier reducing $reduction damage!")
        return reduction
    }

    override fun displayStatus() {
        println("[$name] (Lvl $level) HP: $health/$maxHealth | Mana: $mana/$maxMana")
    }
}

// Archer
class Archer(name: String, health: Int) : BaseCharacter(name, health), Combatant {
    private var arrows = 30

    override fun attack(): Int {
        return if (arrows > 0) {
            arrows--
            val dmg = calculateBaseDamage() + 3
            println("$name shoots arrow for $dmg damage! (Arrows: $arrows)")
            dmg
        } else {
            println("$name has no arrows! Weak attack for 2 damage!")
            2
        }
    }

    override fun performSpecialAttack(): Int {
        return if (arrows >= 3) {
            arrows -= 3
            val dmg = calculateBaseDamage() * 2
            println("$name uses Multi-shot for $dmg damage! (Arrows: $arrows)")
            dmg
        } else 0
    }

    override fun defend(): Int {
        val reduction = 3 + level
        println("$name evades, reducing $reduction damage!")
        return reduction
    }

    fun refillArrows(amount: Int = 20) {
        arrows += amount
        println("$name refills arrows (+$amount). Total: $arrows")
    }
}

// BattleSystem
class BattleSystem {
    fun conductBattle(c1: Character, c2: Character) {
        println("Battle: ${c1.name} vs ${c2.name}")
        while (c1.isAlive() && c2.isAlive()) {
            performTurn(c1, c2)
            if (c2.isAlive()) performTurn(c2, c1)
        }
        println("Winner: ${if (c1.isAlive()) c1.name else c2.name}")
    }

    private fun performTurn(attacker: Character, defender: Character) {
        println("\n${attacker.name}'s turn:")
        val damage = attacker.attack()
        defender.takeDamage(damage)
        defender.displayStatus()
    }
}

// Main
fun main() {
    val warrior = Warrior("Conan", 120)
    val mage = Mage("Gandalf", 80, 100)
    val archer = Archer("Legolas", 100)

    println("=== STATUS ===")
    listOf(warrior, mage, archer).forEach { it.displayStatus() }

    println("\n=== BATTLE ===")
    val battleSystem = BattleSystem()
    battleSystem.conductBattle(warrior, mage)
}
