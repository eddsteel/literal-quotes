{ pkgs ? import <nixpkgs> {} }:
let
  gradle = (pkgs.gradleGen.override (old: { java = pkgs.jdk17; })).gradle_7_3;
  fhs = pkgs.buildFHSUserEnv {
    name = "gradle-env";
    targetPkgs = pkgs:
      (with pkgs; [
        gradle
#        gradle2nix
        kotlin
      ]);
  };
in pkgs.stdenv.mkDerivation {
  name = "gradle-env-shell";
  nativeBuildInputs = [ fhs ];

  shellHook = "exec gradle-env";
}
