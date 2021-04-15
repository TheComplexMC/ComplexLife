package net.thecomplex.complexlife.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;

public class ModelCablePlug<T extends Entity> extends SegmentedModel<T> {
    public final ModelRenderer plug;

    public ModelCablePlug() {
        this.texWidth = 32;
        this.texHeight = 32;
        this.plug = new ModelRenderer(this, 0, 0);
        this.plug.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 1.0F, 3.0F, 0.0F);
        this.plug.addBox(-1.0F, 1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F);
        this.plug.setPos(0, 0, 0);

    }

    @Override
    public Iterable<ModelRenderer> parts() {
        return ImmutableList.of(this.plug);
    }

    @Override
    public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        this.plug.yRot = p_225597_5_ * ((float)Math.PI / 180F);
        this.plug.xRot = p_225597_6_ * ((float)Math.PI / 180F);

    }
}
