# AdditionZ
AdditionZ adds random features to the game.

### Installation
AdditionZ is a mod built for the [Fabric Loader](https://fabricmc.net/). It requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) and [Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config) to be installed separately; all other dependencies are installed with the mod.

### License
AdditionZ is licensed under MIT.

### Datapacks
AdditionZ provides a new recipe type for the fletching table where new recipes can get added.  
If you don't know how to create a datapack check out [Data Pack Wiki](https://minecraft.fandom.com/wiki/Data_Pack) website and try to create your first one for the vanilla game.  
The new recipe files have to be inside a recipe folder, just like vanilla recipes.  
An example for a recipe can be found below:  

```json
{
    "type": "minecraft:fletching",
    "top": {
        "item": "minecraft:flint"
    },
    "middle": {
        "item": "minecraft:stick"
    },
    "bottom": {
        "item": "minecraft:feather"
    },
    "addition": {
        "item": "minecraft:potion",
        "data": "{Potion:\"minecraft:harming\"}"
    },
    "result": {
        "item": "minecraft:tipped_arrow",
        "count": 6,
        "data": "{Potion:\"minecraft:harming\"}"
    }
}
```
Only the "addition" and "result" item can have nbt information.\
The "addition" field is not necessary!

Since v1.2.2 you can now change entity experience drops with a simple datapack.
File must be stored under `data/modid/entity_experience`.
````json
{
    "minecraft:zombie": 200,
    "minecraft:skeleton": 100
}
```
