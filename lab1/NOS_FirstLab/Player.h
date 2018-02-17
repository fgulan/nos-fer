//
//  Player.h
//  NOS_FirstLab
//
//  Created by Filip Gulan on 16/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "FirstLab-swift.h"

@interface Player : NSObject

@property (nonatomic, strong, readonly) BattleshipGround *ground;
@property (nonatomic, strong, readonly) NSString *name;
@property (nonatomic, assign, readonly) int playerID;


+ (instancetype)playerWithID:(int)playerID rows:(UInt32)rows cols:(UInt32)cols ships:(UInt32)ships;

@end
