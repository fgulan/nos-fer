//
//  Player.m
//  NOS_FirstLab
//
//  Created by Filip Gulan on 16/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import "Player.h"

@interface Player ()

@property (nonatomic, strong) BattleshipGround *ground;
@property (nonatomic, assign) int playerID;

@end

@implementation Player

+ (instancetype)playerWithID:(int)playerID rows:(UInt32)rows cols:(UInt32)cols ships:(UInt32)ships
{
    Player *player = [Player new];
    player.ground = [[BattleshipGround alloc] initWithSize:[[GroundSize alloc] initWithRows:rows cols:cols]
                                                shipsCount:ships];
    player.playerID = playerID;
    return player;
}

- (NSString *)name
{
    return [NSString stringWithFormat:@"Player %d", self.playerID];
}

- (NSString *)description
{
    NSString *description = [NSString stringWithFormat:@"%@\n%@", self.name, self.ground.description];
    return description;
}

@end
