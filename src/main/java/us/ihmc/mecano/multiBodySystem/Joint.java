package us.ihmc.mecano.multiBodySystem;

import us.ihmc.euclid.transform.interfaces.RigidBodyTransformReadOnly;
import us.ihmc.mecano.frames.MovingReferenceFrame;
import us.ihmc.mecano.multiBodySystem.interfaces.JointBasics;
import us.ihmc.mecano.multiBodySystem.interfaces.RigidBodyBasics;
import us.ihmc.mecano.tools.MecanoFactories;

/**
 * Base implementation for any {@link JointBasics} that gathers all the basic setup for a joint.
 * <p>
 * A joint represents a part of the robot system that allows a rotation and/or translation of a
 * rigid-body, i.e. the successor, with respect to another rigid-body, i.e. the predecessor.
 * </p>
 * <p>
 * Each joint has a configuration, velocity, acceleration, and force and/or torque. Each of these
 * can be accessed for reading and writing purposes using this interface.
 * </p>
 * <p>
 * Each joint is assigned to reference frames: {@code frameBeforeJoint} and {@code frameAfterJoint}.
 * The transform from {@code frameAfterJoint} to {@code frameBeforeJoint} represents the
 * configuration of this joint. When the joint is at a "zero" configuration, these two reference
 * frames coincide.
 * </p>
 * 
 * @author Twan Koolen
 * @author Sylvain Bertrand
 */
public abstract class Joint implements JointBasics
{
   /**
    * The name of this joint. Each joint of a robot should have a unique name, but it is not enforced
    * here.
    */
   protected final String name;
   /**
    * The identification name for this joint. It is composed of this joint name and all of its
    * ancestors.
    */
   private final String nameId;
   /**
    * The {@code RigidBody} directly connected to this joint and located between this joint and the
    * root body of the robot. The predecessor cannot be {@code null}.
    */
   protected final RigidBodyBasics predecessor;
   /**
    * The {@code RigidBody} directly connected to this joint and located between this joint and an
    * end-effector of the robot. The successor should not be {@code null}, but it is not enforced here.
    */
   protected RigidBodyBasics successor;
   /**
    * Reference frame fixed to the predecessor and which origin is located at this joint's origin.
    */
   protected final MovingReferenceFrame beforeJointFrame;
   /**
    * Reference frame fixed to the successor and which origin is located at this joint's origin.
    */
   protected final MovingReferenceFrame afterJointFrame;

   /**
    * Creates the abstract layer for a new joint.
    * <p>
    * This constructor is usually used for creating a root joint.
    * </p>
    * <p>
    * Note that the {@link #beforeJointFrame} is set to {@code predecessor.getBodyFixedFrame()}.
    * </p>
    * 
    * @param name        the name for the new joint.
    * @param predecessor the rigid-body connected to and preceding this joint.
    */
   public Joint(String name, RigidBodyBasics predecessor)
   {
      this(name, predecessor, null);
   }

   /**
    * Creates the abstract layer for a new joint.
    * 
    * @param name              the name for the new joint.
    * @param predecessor       the rigid-body connected to and preceding this joint.
    * @param transformToParent the transform to the frame after the parent joint. Not modified.
    */
   public Joint(String name, RigidBodyBasics predecessor, RigidBodyTransformReadOnly transformToParent)
   {
      if (name.contains(NAME_ID_SEPARATOR))
         throw new IllegalArgumentException("A joint name can not contain '" + NAME_ID_SEPARATOR + "'. Tried to construct a jonit with name " + name + ".");

      this.name = name;
      this.predecessor = predecessor;
      beforeJointFrame = MecanoFactories.newFrameBeforeJoint(this, transformToParent);
      afterJointFrame = MecanoFactories.newFrameAfterJoint(this);

      if (predecessor.isRootBody())
         nameId = name;
      else
         nameId = predecessor.getParentJoint().getNameId() + NAME_ID_SEPARATOR + name;
      predecessor.addChildJoint(this);
   }

   /** {@inheritDoc} */
   @Override
   public final MovingReferenceFrame getFrameBeforeJoint()
   {
      return beforeJointFrame;
   }

   /** {@inheritDoc} */
   @Override
   public final MovingReferenceFrame getFrameAfterJoint()
   {
      return afterJointFrame;
   }

   /** {@inheritDoc} */
   @Override
   public final RigidBodyBasics getPredecessor()
   {
      return predecessor;
   }

   /** {@inheritDoc} */
   @Override
   public final RigidBodyBasics getSuccessor()
   {
      return successor;
   }

   /** {@inheritDoc} */
   @Override
   public final String getName()
   {
      return name;
   }

   /** {@inheritDoc} */
   @Override
   public String getNameId()
   {
      return nameId;
   }

   /**
    * Returns the implementation name of this joint and the joint name.
    */
   @Override
   public String toString()
   {
      return getClass().getSimpleName() + " " + getName();
   }

   /**
    * The hash code of a joint is based on its {@link #getNameId()}.
    *
    * @return the hash code of the {@link #getNameId()} of this joint.
    */
   @Override
   public int hashCode()
   {
      return nameId.hashCode();
   }
}