package team.unnamed.hephaestus.model;

import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.struct.Vector3Float;

import java.util.List;

/**
 * It's a model cube holder, a {@link ModelCube}
 * composite, util to make rotations over the pivot
 */
public class ModelBone {

    @Nullable private final ModelBone parent;
    private final String name;
    private final Vector3Float rotation;

    private final List<ModelBone> bones;
    private final Vector3Float offset;
    private ModelBoneAsset asset;

    private final int customModelData;
    private final boolean hasCubes;

    public ModelBone(
            @Nullable ModelBone parent,
            String name,
            Vector3Float rotation,
            List<ModelBone> bones,
            Vector3Float offset,
            ModelBoneAsset asset
    ) {
        this.parent = parent;
        this.name = name;
        this.rotation = rotation;
        this.bones = bones;
        this.offset = offset;
        this.asset = asset;

        // data from 'asset' that will persist after calling
        // discardResourcePackData()
        this.customModelData = asset.getCustomModelData();
        this.hasCubes = asset.getCubes().size() > 0;
    }

    @Nullable
    public ModelBoneAsset getAsset() {
        return asset;
    }

    @Nullable
    public ModelBone getParent() {
        return parent;
    }

    public Vector3Float getOffset() {
        return offset;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public boolean hasCubes() {
        return hasCubes;
    }

    public String getName() {
        return name;
    }

    public Vector3Float getRotation() {
        return rotation;
    }

    public List<ModelBone> getBones() {
        return bones;
    }

    public void discardResourcePackData() {
        this.asset = null;
        for (ModelBone bone : bones) {
            bone.discardResourcePackData();
        }
    }

}