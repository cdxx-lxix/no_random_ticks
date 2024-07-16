## Stop blocks from random ticks or ticks

#### Random ticks
Most blocks in the game perform randomTicks and this part is quiet optimized in itself. Yet, the more blocks you have the bigger the strain on a server. Even stairs do randomTick() for some mysterious reason. 
Preventing randomTick() is a somewhat safe option. You just prevent some mechanics from running, like copper weathering, bamboo growing. At least in vanilla I haven't found any breaking blocks that crash the game if they are banned. I have randomly selected 50% of all the blocks in the game and banned them. No issues.

Here is some data from profiler:
  Versions: Minecraft 1.20.1, Forge 4.30.0. 
  Mods: NoRandomTicks and WorldEdit
  Blocks: 3800 bamboo, 5800 acacia stairs, 4200 birch leaves

[How to understand this numbers](https://docs.minecraftforge.net/en/latest/misc/debugprofiler/)

Vanilla:
```
[08] |   |   |   |   |   |   |   |   tickBlocks(40401/201) - 64.88%/5.94%
[09] |   |   |   |   |   |   |   |   |   randomTick(53667/267) - 72.67%/4.32%
[10] |   |   |   |   |   |   |   |   |   |   #getChunkCacheMiss 4309/21
[10] |   |   |   |   |   |   |   |   |   |   #getChunk 97830/486
[10] |   |   |   |   |   |   |   |   |   |   unspecified(53667/267) - 82.14%/3.55%
[10] |   |   |   |   |   |   |   |   |   |   queueCheckLight(4755/24) - 8.67%/0.37%
[10] |   |   |   |   |   |   |   |   |   |   updateSkyLightSources(4755/24) - 8.45%/0.37%
[10] |   |   |   |   |   |   |   |   |   |   no_random_tick_check_blocks(6428/32) - 0.74%/0.03%
```

Blacklisted:
```
[08] |   |   |   |   |   |   |   |   tickBlocks(40401/201) - 40.26%/1.50%
[09] |   |   |   |   |   |   |   |   |   unspecified(40401/201) - 75.91%/1.14%
[09] |   |   |   |   |   |   |   |   |   randomTick(53064/264) - 24.09%/0.36%
[10] |   |   |   |   |   |   |   |   |   |   #getChunkCacheMiss 43/0
[10] |   |   |   |   |   |   |   |   |   |   #getChunk 882/4
[10] |   |   |   |   |   |   |   |   |   |   unspecified(53064/264) - 92.69%/0.34%
[10] |   |   |   |   |   |   |   |   |   |   no_random_tick_check_blocks(5231/26) - 7.31%/0.03%
```

Consider +-1% as error

| Vanilla | Mod | Difference |
| ------------- |:-------------:| -----:|
| tickBlocks(40401/201) - 64.88%/5.94% | tickBlocks(40401/201) - 40.26%/1.50% | +4.44% |
| randomTick(53667/267) - 72.67%/4.32% | randomTick(53064/264) - 24.09%/0.36% | +3.96% |
| no_random_tick_check_blocks(6428/32) - 0.74%/0.03% | no_random_tick_check_blocks(5231/26) - 7.31%/0.03% | no change |

#### Ticks
This territory is full of surprizes. Some blocks may be unaffected at all while others may stop working or only half of the functionality may be gone. E.g. minecraft:fire, if you ban it from ticking the only thing that will happen is it won't destroy block (if set on wood for example) and extinguish. So, basically behaves like on netherrack. 
Furnaces don't give a shit. So, unless you know how a block works or ready for a long process of trail & error, I recommend to avoid this feature. 

#### Fluids
I couldn't prevent them from ticking because they have a very specific logic tied to this method which is unique to every liquid. So, if you prevent it from happening the game crashes. 
If you know how to do that, please tell me. 

## Usage
The way I see it, this mod may help you:
- Squeeze some performance if you are in a really tight spot. This is for a server, remember, this mod helps the server way more than to a client. 
- Prevent some mechanics from happening while leaving the block.
- Debugging


