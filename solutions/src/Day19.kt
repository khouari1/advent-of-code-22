import java.util.*

fun main() {
    fun part1(input: List<String>): Int =
        getBlueprintsFrom(input)
            .sumOf { blueprint ->
                val robots = listOf(Robot.OreRobot)
                val geodes = getHighestGeodeCount(blueprint, robots, 24)
                blueprint.id * geodes
            }

    fun part2(input: List<String>): Int =
        getBlueprintsFrom(input)
            .take(3)
            .map { blueprint ->
                val robots = listOf(Robot.OreRobot)
                val geodes = getHighestGeodeCount(blueprint, robots, 32)
                geodes
            }.reduce { acc, i -> acc * i }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 33)
    check(part2(testInput) == 3472)

    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}

private fun getHighestGeodeCount(blueprint: Blueprint, robots: List<Robot>, totalMinutes: Int): Int {
    val stack = Stack<State>()
    stack.add(State(RobotFactory(blueprint), robots, 1))
    val maxOreRequiredPerTurn = maxOf(
        blueprint.oreRobotCost.ore,
        blueprint.clayRobotCost.ore,
        blueprint.obsidianRobotCost.ore,
        blueprint.geodeRobotCost.ore,
    )
    val maxClayRequiredPerTurn = maxOf(
        blueprint.oreRobotCost.clay,
        blueprint.clayRobotCost.clay,
        blueprint.obsidianRobotCost.clay,
        blueprint.geodeRobotCost.clay,
    )
    val maxObsidianRequiredPerTurn = maxOf(
        blueprint.oreRobotCost.obsidian,
        blueprint.clayRobotCost.obsidian,
        blueprint.obsidianRobotCost.obsidian,
        blueprint.geodeRobotCost.obsidian,
    )
    var highestGeodes = 0
    // DFS to find highest geode count
    while (stack.isNotEmpty()) {
        val (robotFactory, robots, minute, skippedOre, skippedClay, skippedObsidian) = stack.pop()
        val oreRobotsCount = robots.filterIsInstance<Robot.OreRobot>().count()
        val clayRobotsCount = robots.filterIsInstance<Robot.ClayRobot>().count()
        val obsidianRobotsCount = robots.filterIsInstance<Robot.ObsidianRobot>().count()
        if (minute > totalMinutes) {
            if (robotFactory.materials.geodes > highestGeodes) {
                highestGeodes = robotFactory.materials.geodes
            }
            continue
        }
        val newMaterials = perTurnMaterialsCollected(robots)
        // If can create geode robot then just do that
        if (robotFactory.canCreateRobot(Robot.GeodeRobot)) {
            val newFactory = robotFactory.copy()
            newFactory.createRobot(Robot.GeodeRobot)
            stack.add(State(newFactory, robots + Robot.GeodeRobot, minute + 1))
            newFactory.addMaterials(newMaterials)
        } else {
            // If a robot could have been built but was skipped then don't build it until a different robot has been built
            // Also if the number of a certain robot means the max required material is available each turn then no need to build more
            val canCreateObsidian =
                robotFactory.canCreateRobot(Robot.ObsidianRobot) && obsidianRobotsCount < maxObsidianRequiredPerTurn
            if (canCreateObsidian && !skippedObsidian) {
                val newFactory = robotFactory.copy()
                newFactory.createRobot(Robot.ObsidianRobot)
                stack.add(State(newFactory, robots + Robot.ObsidianRobot, minute + 1))
                newFactory.addMaterials(newMaterials)
            }
            val canCreateClay =
                robotFactory.canCreateRobot(Robot.ClayRobot) && clayRobotsCount < maxClayRequiredPerTurn
            if (canCreateClay && !skippedClay) {
                val newFactory = robotFactory.copy()
                newFactory.createRobot(Robot.ClayRobot)
                stack.add(State(newFactory, robots + Robot.ClayRobot, minute + 1))
                newFactory.addMaterials(newMaterials)
            }
            val canCreateOre = robotFactory.canCreateRobot(Robot.OreRobot) && oreRobotsCount < maxOreRequiredPerTurn
            if (canCreateOre && !skippedOre) {
                val newFactory = robotFactory.copy()
                newFactory.createRobot(Robot.OreRobot)
                stack.add(State(newFactory, robots + Robot.OreRobot, minute + 1))
                newFactory.addMaterials(newMaterials)
            }
            val newFactory = robotFactory.copy()
            stack.add(State(newFactory, robots, minute + 1, canCreateOre, canCreateClay, canCreateObsidian))
            newFactory.addMaterials(newMaterials)
        }
    }
    return highestGeodes
}

private fun perTurnMaterialsCollected(robots: List<Robot>): Materials {
    return if (robots.isEmpty()) {
        Materials()
    } else {
        robots.map {
            when (it) {
                is Robot.ClayRobot -> Materials(clay = 1)
                is Robot.GeodeRobot -> Materials(geodes = 1)
                is Robot.ObsidianRobot -> Materials(obsidian = 1)
                is Robot.OreRobot -> Materials(ore = 1)
            }
        }.reduce { acc, materials -> acc + materials }
    }
}

data class State(
    val robotFactory: RobotFactory,
    val robots: List<Robot>,
    val minute: Int,
    val skippedOre: Boolean = false,
    val skippedClay: Boolean = false,
    val skippedObsidian: Boolean = false,
)

data class RobotFactory(
    private val blueprint: Blueprint,
    var materials: Materials = Materials(),
) {
    fun addMaterials(materials: Materials) {
        this.materials = this.materials + materials
    }

    fun canCreateRobot(robot: Robot): Boolean {
        val (ore, clay, obsidian) = when (robot) {
            Robot.ClayRobot -> blueprint.clayRobotCost
            Robot.GeodeRobot -> blueprint.geodeRobotCost
            Robot.ObsidianRobot -> blueprint.obsidianRobotCost
            Robot.OreRobot -> blueprint.oreRobotCost
        }
        return materials.clay >= clay &&
                materials.ore >= ore &&
                materials.obsidian >= obsidian
    }

    fun createRobot(robot: Robot) {
        materials -= when (robot) {
            Robot.ClayRobot -> blueprint.clayRobotCost
            Robot.GeodeRobot -> blueprint.geodeRobotCost
            Robot.ObsidianRobot -> blueprint.obsidianRobotCost
            Robot.OreRobot -> blueprint.oreRobotCost
        }
    }
}

data class Blueprint(
    val id: Int,
    val oreRobotCost: MaterialCost,
    val clayRobotCost: MaterialCost,
    val obsidianRobotCost: MaterialCost,
    val geodeRobotCost: MaterialCost,
)

data class MaterialCost(
    val ore: Int,
    val clay: Int = 0,
    val obsidian: Int = 0,
)

data class Materials(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geodes: Int = 0,
) {
    operator fun plus(materials: Materials): Materials {
        return Materials(
            ore = this.ore + materials.ore,
            clay = this.clay + materials.clay,
            obsidian = this.obsidian + materials.obsidian,
            geodes = this.geodes + materials.geodes,
        )
    }

    operator fun minus(materials: MaterialCost): Materials {
        return Materials(
            ore = this.ore - materials.ore,
            clay = this.clay - materials.clay,
            obsidian = this.obsidian - materials.obsidian,
            geodes = geodes,
        )
    }
}

sealed class Robot {
    object OreRobot : Robot()
    object ClayRobot : Robot()
    object ObsidianRobot : Robot()
    object GeodeRobot : Robot()
}

private fun getBlueprintsFrom(input: List<String>): List<Blueprint> {
    return input.map { line ->
        val split = line.split(" ")
        val blueprintId = split[1].dropLast(1).toInt()
        val oreRobotCost = MaterialCost(ore = split[6].toInt())
        val clayRobotCost = MaterialCost(ore = split[12].toInt())
        val obsidianRobotCost = MaterialCost(ore = split[18].toInt(), clay = split[21].toInt())
        val geodeRobotCost = MaterialCost(ore = split[27].toInt(), obsidian = split[30].toInt())
        Blueprint(
            id = blueprintId,
            oreRobotCost = oreRobotCost,
            clayRobotCost = clayRobotCost,
            obsidianRobotCost = obsidianRobotCost,
            geodeRobotCost = geodeRobotCost,
        )
    }
}