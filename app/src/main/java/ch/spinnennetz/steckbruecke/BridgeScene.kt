package ch.spinnennetz.steckbruecke

import com.pico.spatial.core.ecs.CollisionComponent
import com.pico.spatial.core.ecs.DirectionalLightComponent
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.InteractableComponent
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.ecs.resource.PhysicsMaterialResource
import com.pico.spatial.core.ecs.resource.ShapeResource
import com.pico.spatial.core.math.Color4
import com.pico.spatial.core.math.EulerAngles
import com.pico.spatial.core.math.Vector3

internal sealed interface WorkbenchTarget {
    val part: Part
    data class LoosePart(override val part: Part) : WorkbenchTarget
    data class Socket(override val part: Part) : WorkbenchTarget
}

internal class BridgeScene private constructor(
    val root: Entity,
    private val pieces: Map<Part, Entity>,
    private val sockets: Map<Part, Entity>,
    private val shapeResources: List<ShapeResource>,
    private val physicsMaterial: PhysicsMaterialResource,
) {
    private var lastAssembly: Assembly? = null

    fun show(assembly: Assembly) {
        if (assembly == lastAssembly) return
        for (part in Part.entries) {
            val fitted = assembly.isPlaced(part)
            pieces.getValue(part).transform().apply {
                setPosition(if (fitted) finalPosition.getValue(part) else trayPosition.getValue(part))
                val scale = if (assembly.selected == part) 1.07f else 1f
                setScaleVector(Vector3(scale, scale, scale))
            }

            val ring = sockets.getValue(part)
            ring.enabled = !fitted
            val ringScale = if (assembly.next == part) 1.25f else 0.62f
            ring.transform().setScaleVector(Vector3(ringScale, ringScale, ringScale))
        }
        lastAssembly = assembly
    }

    fun target(entity: Entity): WorkbenchTarget? {
        var candidate: Entity? = entity
        while (candidate != null && candidate != root) {
            pieces.entries.firstOrNull { it.value == candidate }?.let {
                return WorkbenchTarget.LoosePart(it.key)
            }
            sockets.entries.firstOrNull { it.value == candidate }?.let {
                return WorkbenchTarget.Socket(it.key)
            }
            candidate = candidate.getParent()
        }
        return null
    }

    fun close() {
        if (root.valid) {
            root.removeFromParent()
            root.destroy()
        }
        shapeResources.filter { it.valid }.forEach { it.close() }
        if (physicsMaterial.valid) physicsMaterial.close()
    }

    companion object {
        // Authored GLB translations in metres. The original base stays fixed.
        private val finalPosition = mapOf(
            Part.LEFT to Vector3(-0.126f, 0.096f, 0f),
            Part.RIGHT to Vector3(0.126f, 0.096f, 0f),
            Part.BEAM to Vector3(0f, 0.175f, 0f),
        )
        private val trayPosition = mapOf(
            Part.LEFT to Vector3(-0.31f, 0.090f, 0.23f),
            Part.RIGHT to Vector3(0.31f, 0.090f, 0.23f),
            Part.BEAM to Vector3(0f, 0.034f, 0.25f),
        )
        private val socketPosition = mapOf(
            Part.LEFT to Vector3(-0.126f, 0.043f, 0f),
            Part.RIGHT to Vector3(0.126f, 0.043f, 0f),
            Part.BEAM to Vector3(0f, 0.162f, 0f),
        )

        suspend fun load(): BridgeScene {
            val root = Entity()
            val material = PhysicsMaterialResource()
            val shapes = mutableListOf<ShapeResource>()
            try {
                root.transform().setPosition(Vector3(0f, -0.12f, 0f))
                root.addChild(directionalLight(-42f, -30f, 1800f))
                root.addChild(directionalLight(-15f, 38f, 650f))
                val bridge = Entity.loadSuspend("asset://steckbruecke.glb")
                root.addChild(bridge)
                val pieces = Part.entries.associateWith { part ->
                    val entity = requireNotNull(bridge.findEntity(part.meshName)) {
                        "Missing GLB node ${part.meshName}"
                    }
                    val dimensions = if (part == Part.BEAM) {
                        Vector3(0.37f, 0.06f, 0.08f)
                    } else {
                        Vector3(0.10f, 0.15f, 0.10f)
                    }
                    entity.addTapTarget(dimensions, material, shapes)
                    entity
                }
                val sockets = Part.entries.associateWith { part ->
                    val marker = Entity.loadSuspend("asset://placement-ring.glb")
                    root.addChild(marker)
                    marker.transform().setPosition(socketPosition.getValue(part))
                    marker.addTapTarget(Vector3(0.115f, 0.047f, 0.115f), material, shapes)
                    marker
                }
                return BridgeScene(root, pieces, sockets, shapes, material)
            } catch (error: Exception) {
                if (root.valid) root.destroy()
                shapes.filter { it.valid }.forEach { it.close() }
                if (material.valid) material.close()
                throw error
            }
        }

        private fun directionalLight(pitch: Float, yaw: Float, strength: Float): Entity =
            Entity().apply {
                transform().setEulerAngles(EulerAngles(pitch = pitch, yaw = yaw))
                components.set(
                    DirectionalLightComponent(
                        color = Color4(1f, 1f, 1f, 1f),
                        intensity = strength,
                    ),
                )
            }
    }
}

private fun Entity.transform(): TransformComponent =
    requireNotNull(components.get<TransformComponent>())

private fun Entity.addTapTarget(
    dimensions: Vector3,
    material: PhysicsMaterialResource,
    shapes: MutableList<ShapeResource>,
) {
    val shape = ShapeResource.createBox(dimensions)
    shapes.add(shape)
    components.set(InteractableComponent())
    components.set(CollisionComponent(collisionShape = listOf(shape), physicsMaterial = material))
}
