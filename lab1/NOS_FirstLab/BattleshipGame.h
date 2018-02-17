//
//  BattleshipGame.h
//  NOS_FirstLab
//
//  Created by Filip Gulan on 16/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface BattleshipGame : NSObject

+ (instancetype)gameWithShips:(UInt32)ships rows:(UInt32)rows cols:(UInt32)cols playerID:(int)playerID;

- (void)start;

@end
