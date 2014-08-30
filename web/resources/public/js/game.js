var MAX_SPEED = 500;     // Pixels / second
var JUMP_SPEED = -250;   // Pixels / second (negative y is u p )
var ACCELERATION = 1200; // Pixels / second / second
var DRAG = 2400;         // Pixels / second / second
var GRAVITY = 2400;      // Pixels / second

var gameIsPaused = false;

var GameState = function(game) {};

// Preload assets that can be loaded.
GameState.prototype.preload = function() {
  this.game.load.image('player', 'assets/sprites/quester.png');
  this.game.load.image('enemy', 'assets/sprites/first-enemy.png');
  this.game.load.tilemap('world', 'assets/world.json', null, Phaser.Tilemap.TILED_JSON);
};

GameState.prototype.create = function() {
  // Register inputs from keyboard
  this.game.input.keyboard.addKeyCapture([
    Phaser.Keyboard.LEFT,
    Phaser.Keyboard.RIGHT,
    Phaser.Keyboard.UP,
    Phaser.Keyboard.DOWN,
  ]);
  this.input = new Input();

  // FIXME Implement gradient background
  // var bgBackground = this.game.add.bitmapData(100, 100);
  // bgBackground.beginLinearGradientFill(["#000", "#FFF"], [0, 1], 0, 20, 0, 120);
  // bgBackground.rect(20, 20, 120, 120);
  // bgBackground.fill();
  // this.game.add.sprite(50, 50, bgbackground);

  // Set background
  this.game.stage.backgroundColor = '#7ec0ee';

  // Splice together tiles and background sprite sheets
  this.map = this.game.add.tilemap('world');

  this.player = this.game.add.sprite(this.game.width / 2,
                                     this.game.height - this.game.height / 1.33,
                                     'player');
  this.game.physics.enable(this.player,
                           Phaser.Physics.ARCADE);

  this.player.body.collideWorldBounds = true;
  this.player.body.maxVelocity.setTo(MAX_SPEED, MAX_SPEED * 10); // (x, y)
  this.player.body.drag.setTo(DRAG, 0); // (x, y)

  this.game.physics.arcade.gravity.y = GRAVITY;

  // Create FPS counter
  this.game.time.advancedTiming = true;
  this.fpsText = this.game.add.text(20, 20, '', {font: '16px Arial', fill: '#ffffff'});
};

GameState.prototype.update = function() {
  if (!gameIsPaused) {

    // Handle input
    if (this.input.isLeft()) {
      this.player.body.acceleration.x = -ACCELERATION;
    } else if (this.input.isRight()){
      this.player.body.acceleration.x = ACCELERATION;
    } else {
      this.player.body.acceleration.x = 0;
    }

    if (this.input.isUp() && this.player.body.touching.down) {
      this.player.body.velocity.y = JUMP_SPEED;
    }

    // Update FPS Counter
    if (this.game.time.fps !== 0) {
      this.fpsText.setText(this.game.time.fps + ' FPS');
    }
  }
};

var game = new Phaser.Game(800, 600, Phaser.AUTO, 'game');
game.state.add('game', GameState, true); // What does true do here?

function Input() {

  this.isLeft = function() {
    var isActive = false;

    isActive = game.input.keyboard.isDown(Phaser.Keyboard.LEFT);
    isActive |= (game.input.activePointer.isDown &&
                 game.input.activePointer.x < game.width / 4);

    return isActive;
  };

  this.isRight = function() {
    var isActive = false;

    isActive = game.input.keyboard.isDown(Phaser.Keyboard.RIGHT);
    isActive |= (game.input.activePointer.isDown &&
                 game.input.activePointer.x > game.width / 2 + game.width / 4);

    return isActive;
  };

  // FIXME Still includes duration
  this.isUp = function (duration) {
    var isActive = false;

    isActive = game.input.keyboard.justPressed(Phaser.Keyboard.UP, duration);
    isActive |= (game.input.activePointer.justPressed(duration + 1000/60) &&
                 game.input.activePointer.x > game.width / 4 &&
                 game.input.activePointer.x < game.width / 2 + game.width / 4);

    return isActive;
  };
}
