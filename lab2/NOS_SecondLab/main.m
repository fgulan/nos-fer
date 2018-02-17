//
//  main2.m
//  NOS_SecondLab
//
//  Created by Filip Gulan on 27/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Process.h"

int main(int argc, const char * argv[])
{
    @autoreleasepool
    {
        Process *process = [[Process alloc] initWithCount:3];
        [process start];
        exit(0);
    }
    return 0;
}
