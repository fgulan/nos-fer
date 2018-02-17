//
//  Timestamp.h
//  NOS_SecondLab
//
//  Created by Filip Gulan on 20/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Timestamp : NSObject

@property (nonatomic, assign, readonly) NSInteger sourceID;
@property (nonatomic, assign, readonly) NSInteger value;

+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;
- (instancetype)initWithSourceID:(NSInteger)sourceID value:(NSInteger)value NS_DESIGNATED_INITIALIZER;

@end
