//
//  main.m
//  NOS_FirstLab
//
//  Created by Filip Gulan on 16/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "FirstLab-swift.h"
#import "BattleshipGame.h"

int main(int argc, const char * argv[]) {
    @autoreleasepool {
        if (argc > 1) {
            BattleshipGame *game = [BattleshipGame gameWithShips:5 rows:4 cols:4 playerID:atoi(argv[1])];
            [game start];
        }
    }
    return 0;
}
