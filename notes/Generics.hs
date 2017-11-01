module Generics where

import qualified Data.List as DL

data TypeParameter
  = TyPar String (Maybe TypeBound)
  deriving (Eq, Show)

data TypeBound
  = TyBoVar TypeVariable
  | TyBoRef ClassType [ClassType]
  deriving (Eq, Show)

data ReferenceType
  = RtClass ClassType
  | RtTyVar TypeVariable
  | RtArray ArrayType
  deriving (Eq, Show)

data ClassType
  = CtType String [TypeArgument]
  deriving (Eq, Show)

data ArrayType
  = AtClass ClassType
  | AtVar TypeVariable
  | AtPrim String
  deriving (Eq, Show)

data TypeVariable
  = TyVar String
  deriving (Eq, Show)

data TypeArgument
  = TaRef  ReferenceType
  | TaWild Wildcard
  deriving (Eq, Show)

data Wildcard
  = WExtends ReferenceType
  | WSuper ReferenceType
  deriving (Eq, Show)

showRef :: ReferenceType -> String
showRef (RtClass c) = showClass c
showRef (RtTyVar t) = showTyVar t
showRef (RtArray a) = showArray a

showTyArg :: TypeArgument -> String
showTyArg (TaRef r)  = showRef r
showTyArg (TaWild w) = showWild w

showWild :: Wildcard -> String
showWild (WExtends t) = "? extends " ++ (showRef t)
showWild (WSuper t) = "? super " ++ (showRef t)

showClass :: ClassType -> String
showClass (CtType name args) = name ++ "<" ++ (DL.intercalate "," (map showTyArg args)) ++ ">"

showTyVar :: TypeVariable -> String
showTyVar (TyVar n) = n

showArray :: ArrayType -> String
showArray (AtClass c) = (showClass c) ++ "[]"
showArray (AtVar v) = (showTyVar v) ++ "[]"
showArray (AtPrim t) = t ++ "[]"

showTyPar :: TypeParameter -> String
showTyPar (TyPar name Nothing) = name
showTyPar (TyPar name (Just bound)) = name ++ showJava bound

showTyBound :: TypeBound -> String
showTyBound (TyBoVar v) = " extends " ++ showTyVar v
showTyBound (TyBoRef r xs) = " extends " ++ (showClass r) ++ (DL.intercalate "&" (map showClass xs))

class ShowJava a where
  showJava :: a -> String

instance ShowJava TypeParameter where
  showJava = showTyPar
instance ShowJava TypeBound where
  showJava = showTyBound
instance ShowJava ReferenceType where
  showJava = showRef
instance ShowJava TypeArgument where
  showJava = showTyArg
instance ShowJava Wildcard where
  showJava = showWild
instance ShowJava ClassType where
  showJava = showClass
instance ShowJava TypeVariable where
  showJava = showTyVar
instance ShowJava ArrayType where
  showJava = showArray

