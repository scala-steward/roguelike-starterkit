package demo

import indigo.*
import indigo.scenes.*
import indigoextras.subsystems.FPSCounter
import roguelikestarterkit.*

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object RogueLikeGame extends IndigoGame[Size, Size, Model, ViewModel]:

  val magnification: Int = 2

  def initialScene(bootData: Size): Option[SceneName] =
    Option(UIScene.name)

  def scenes(bootData: Size): NonEmptyList[Scene[Size, Model, ViewModel]] =
    NonEmptyList(
      UISubSystemScene,
      UIScene,
      LightingScene,
      RogueTerminalEmulatorScene,
      TerminalTextScene,
      TerminalEmulatorScene
    )

  val eventFilters: EventFilters =
    EventFilters.Permissive

  def boot(flags: Map[String, String]): Outcome[BootResult[Size, Model]] =
    Outcome(
      BootResult(
        Config.config.withMagnification(magnification),
        Config.config.viewport.size / 2
      )
        .withFonts(RoguelikeTiles.Size10x10.Fonts.fontInfo)
        .withAssets(Assets.assets.assetSet)
        .withShaders(
          shaders.all ++ Set(
            TerminalTextScene.customShader(ShaderId("my shader"))
          )
        )
        .withSubSystems(FPSCounter(Point(10, 350)))
    )

  def initialModel(startupData: Size): Outcome[Model] =
    Outcome(Model.initial)

  def initialViewModel(startupData: Size, model: Model): Outcome[ViewModel] =
    Outcome(ViewModel.initial)

  def setup(bootData: Size, assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Size]] =
    Outcome(Startup.Success(bootData))

  def updateModel(context: FrameContext[Size], model: Model): GlobalEvent => Outcome[Model] =
    _ => Outcome(model)

  def updateViewModel(
      context: FrameContext[Size],
      model: Model,
      viewModel: ViewModel
  ): GlobalEvent => Outcome[ViewModel] =
    _ => Outcome(viewModel)

  def present(
      context: FrameContext[Size],
      model: Model,
      viewModel: ViewModel
  ): Outcome[SceneUpdateFragment] =
    Outcome(SceneUpdateFragment.empty)

final case class Model(windowManager: WindowManagerModel[Unit])

object Model:

  val defaultCharSheet: CharSheet =
    CharSheet(
      Assets.assets.AnikkiSquare10x10,
      Size(10),
      RoguelikeTiles.Size10x10.charCrops,
      RoguelikeTiles.Size10x10.Fonts.fontKey
    )

  val initial: Model =
    Model(
      WindowManagerModel.initial
        .register(
          ColourWindow.window(
            defaultCharSheet
          )
        )
        .open(ColourWindow.windowId)
    )

final case class ViewModel(windowManager: WindowManagerViewModel[Unit])
object ViewModel:
  def initial: ViewModel =
    ViewModel(WindowManagerViewModel.initial(RogueLikeGame.magnification))
