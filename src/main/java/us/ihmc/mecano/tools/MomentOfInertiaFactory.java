package us.ihmc.mecano.tools;

import us.ihmc.euclid.matrix.Matrix3D;
import us.ihmc.euclid.tuple3D.Vector3D;
import us.ihmc.euclid.tuple3D.interfaces.Tuple3DReadOnly;
import us.ihmc.euclid.tuple3D.interfaces.Vector3DReadOnly;

/**
 * This class contains tools to obtain the moment of inertia matrix of primitive shapes.
 */
public class MomentOfInertiaFactory
{
   /**
    * Computes the moment of inertia matrix for a solid cylinder.
    * 
    * @param mass           the cylinder mass.
    * @param radius         the radius of the cylinder.
    * @param height         the height, or length, of the cylinder.
    * @param axisOfCylinder the revolution axis of the cylinder. Not modified.
    * @return the moment of inertia of the cylinder.
    */
   public static Matrix3D solidCylinder(double mass, double radius, double height, Vector3DReadOnly axisOfCylinder)
   {
      checkMassAndDimensions(mass, radius, height);

      double IalongAxis = 0.5 * mass * radius * radius;
      double IcrossAxis = mass * (3.0 * radius * radius + height * height) / 12.0;

      Vector3D principalInertia = new Vector3D(1.0, 1.0, 1.0);
      principalInertia.sub(axisOfCylinder);
      principalInertia.scale(IcrossAxis);
      principalInertia.scaleAdd(IalongAxis, axisOfCylinder, principalInertia);

      Matrix3D momentOfInertia = new Matrix3D();
      momentOfInertia.setM00(principalInertia.getX());
      momentOfInertia.setM11(principalInertia.getY());
      momentOfInertia.setM22(principalInertia.getZ());
      return momentOfInertia;

   }

   /**
    * Computes the moment of inertia matrix for a solid ellipsoid.
    * 
    * @param mass  the ellipsoid mass.
    * @param radii the three radii of the ellipsoid. Not modified.
    * @return the moment of inertia of the ellipsoid.
    */
   public static Matrix3D solidEllipsoid(double mass, Tuple3DReadOnly radii)
   {
      return solidEllipsoid(mass, radii.getX(), radii.getY(), radii.getZ());
   }

   /**
    * Computes the moment of inertia matrix for a solid ellipsoid.
    * 
    * @param mass    the ellipsoid mass.
    * @param xRadius radius of the ellipsoid along the x-axis.
    * @param yRadius radius of the ellipsoid along the y-axis.
    * @param zRadius radius of the ellipsoid along the z-axis.
    * @return the moment of inertia of the ellipsoid.
    */
   public static Matrix3D solidEllipsoid(double mass, double xRadius, double yRadius, double zRadius)
   {
      checkMassAndDimensions(mass, xRadius, yRadius, zRadius);
      double ixx = 1.0 / 5.0 * mass * (yRadius * yRadius + zRadius * zRadius);
      double iyy = 1.0 / 5.0 * mass * (zRadius * zRadius + xRadius * xRadius);
      double izz = 1.0 / 5.0 * mass * (xRadius * xRadius + yRadius * yRadius);

      Matrix3D momentOfInertia = new Matrix3D();
      momentOfInertia.setM00(ixx);
      momentOfInertia.setM11(iyy);
      momentOfInertia.setM22(izz);
      return momentOfInertia;
   }

   private static void checkMassAndDimensions(double mass, double... dimensions)
   {
      if (mass < 0.0)
         throw new IllegalArgumentException("can not pass in negative mass values");

      for (double dimension : dimensions)
      {
         if (dimension < 0.0)
            throw new IllegalArgumentException("can not pass in negative dimensions");
      }
   }
}
