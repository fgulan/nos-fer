//
//  Tuple.h
//  NOS_SecondLab
//
//  Created by Filip Gulan on 20/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Tuple : NSObject <NSCopying>

@property (nonatomic, strong, readonly) id first;
@property (nonatomic, strong, readonly) id second;

+ (instancetype)tupleWithFirst:(id)first second:(id)second;

@end
