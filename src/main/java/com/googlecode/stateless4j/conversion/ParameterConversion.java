package com.googlecode.stateless4j.conversion;
import com.googlecode.stateless4j.validation.Enforce;

    public class ParameterConversion
    {
        public static Object Unpack(Object[] args, Class<?> argType, int index) throws Exception
        {
            Enforce.ArgumentNotNull(args, "args");

            if (args.length <= index)
                throw new Exception(
                    String.format(ParameterConversionResources.ArgOfTypeRequiredInPosition, argType, index));

            Object arg = args[index];

            if (arg != null && !argType.isAssignableFrom(arg.getClass()))
                throw new Exception(
                    String.format(ParameterConversionResources.WrongArgType, index, arg.getClass(), argType));

            return arg;
        }

        public static void Validate(Object[] args, Class<?>[] expected) throws Exception
        {
            if (args.length > expected.length)
                throw new Exception(
                    String.format(ParameterConversionResources.TooManyParameters, expected.length, args.length));

            for (int i = 0; i < expected.length; ++i)
                Unpack(args, expected[i], i);
        }
    }
